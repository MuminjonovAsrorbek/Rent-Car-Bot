package uz.dev.rentcarbot.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.dev.rentcarbot.client.AuthClient;
import uz.dev.rentcarbot.client.UserClient;
import uz.dev.rentcarbot.entity.TelegramUser;
import uz.dev.rentcarbot.enums.RoleEnum;
import uz.dev.rentcarbot.enums.StepEnum;
import uz.dev.rentcarbot.payload.CarDTO;
import uz.dev.rentcarbot.payload.TokenDTO;
import uz.dev.rentcarbot.payload.UserDTO;
import uz.dev.rentcarbot.repository.TelegramUserRepository;
import uz.dev.rentcarbot.service.template.*;
import uz.dev.rentcarbot.utils.CommonUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by: asrorbek
 * DateTime: 8/5/25 16:34
 **/

@Service
@RequiredArgsConstructor
public class TextServiceImpl implements TextService {

    private final TelegramUserRepository userRepository;
    private final ReplyButtonService replyButtonService;
    private final InlineButtonService inlineButtonService;
    private final AuthClient authClient;
    private final TokenService tokenService;
    private final UserClient userClient;
    private final ConversationStateService conversationStateService;

    @Override
    @Transactional
    public BotApiMethod<?> processText(Message message) {

        String text = message.getText();
        Long chatId = message.getChatId();

        if (text.startsWith("/")) {
            if (text.equals("/start")) {
                Optional<TelegramUser> userOptional = userRepository.findByChatId(message.getChatId());
                if (userOptional.isEmpty()) {
                    ReplyKeyboardMarkup buttonMarkup = replyButtonService.buildPhoneNumber();
                    TelegramUser user = new TelegramUser();
                    user.setChatId(chatId);
                    user.setFirstName(message.getChat().getFirstName());
                    user.setUsername(CommonUtils.getOrDef(message.getChat().getUserName(), null));
                    user.setStep(StepEnum.SEND_PHONE_NUMBER);
                    user.setRole(RoleEnum.USER);
                    userRepository.save(user);
                    return SendMessage.builder().chatId(chatId).text("🎉 Xush kelibsiz, @RentCarBot ga!\nRo‘yxatdan o‘tish uchun telefon raqamingizni yuboring.\nBu sizning ijaralaringizni boshqarish va xizmatlarimizdan foydalanish uchun zarur!").replyMarkup(buttonMarkup).build();
                } else {
                    TelegramUser user = userOptional.get();
                    TokenDTO tokenDTO = authClient.getTokenByPhoneNumber(user.getPhoneNumber());
                    tokenService.saveTokens(chatId, tokenDTO);
                    user.setStep(StepEnum.SELECT_MENU);
                    user.setRole(RoleEnum.USER);
                    userRepository.save(user);
                    return SendMessage.builder().chatId(user.getChatId()).text("Asosiy menyuga xush kelibsiz!").replyMarkup(replyButtonService.buildMenuButtons(user.getRole())).build();
                }
            }

            if (text.equals("/admin")) {
                try {
                    tokenService.getAccessToken(chatId);
                    UserDTO userDto = userClient.getMe();
                    if (userDto.getRoles() != null && userDto.getRoles().contains("ROLE_ADMIN")) {
                        TelegramUser botUser = userRepository.findByChatIdOrThrowException(chatId);
                        botUser.setStep(StepEnum.ADMIN_MENU);
                        botUser.setRole(RoleEnum.ADMIN);
                        userRepository.save(botUser);
                        return SendMessage.builder().chatId(chatId).text("Salom, Admin. Kerakli bo'limni tanlang:").replyMarkup(replyButtonService.buildAdminMenuButtons()).build();
                    } else {
                        return SendMessage.builder().chatId(chatId).text("Sizda admin huquqlari mavjud emas.").build();
                    }
                } catch (Exception e) {
                    return SendMessage.builder().chatId(chatId).text("Admin paneliga kirishdan oldin /start buyrug'i orqali tizimga kirishingiz kerak.").build();
                }
            }
        }

        else {
            TelegramUser user = userRepository.findByChatIdOrThrowException(chatId);
            StepEnum currentStep = user.getStep();

            if (currentStep.equals(StepEnum.SELECT_MENU)) {
                if (text.equals("\uD83D\uDD11 Ijaraga olish")) {
                    return SendMessage.builder().chatId(chatId).text("<b>\uD83D\uDE97 Ijaraga olish uchun mavjud mashinalar:</b>\n<i>Quyidagi tugmani bosing va hozirda band bo‘lmagan avtomobillar ro‘yxatini ko‘ring.</i>\s").parseMode(ParseMode.HTML).replyMarkup(inlineButtonService.buildAvailableCars()).build();
                }
            }

            else if (currentStep.equals(StepEnum.ADMIN_MENU)) {
                if (text.equals("Mashinalarni boshqarish")) {
                    return SendMessage.builder().chatId(chatId).text("Mashinalarni boshqarish bo'limi. Kerakli amalni tanlang:").replyMarkup(inlineButtonService.buildAdminCarMenu()).build();
                } else if (text.equals("Foydalanuvchilarni ko'rish")) {
                    return SendMessage.builder().chatId(chatId).text("Foydalanuvchilarni boshqarish bo'limi. Hozirda ishlab chiqilmoqda.").build();
                } else if (text.equals("⬅️ Oddiy menyuga qaytish")) {
                    user.setStep(StepEnum.SELECT_MENU);
                    user.setRole(RoleEnum.USER);
                    userRepository.save(user);
                    return SendMessage.builder().chatId(chatId).text("Oddiy foydalanuvchi menyusiga qaytdingiz.").replyMarkup(replyButtonService.buildMenuButtons(RoleEnum.USER)).build();
                }
            }

            else if (currentStep == StepEnum.ADMIN_ADDING_CAR_BRAND) {
                CarDTO carState = conversationStateService.getState(chatId);
                carState.setBrand(text);
                conversationStateService.updateState(chatId, carState);
                user.setStep(StepEnum.ADMIN_ADDING_CAR_MODEL);
                userRepository.save(user);
                return SendMessage.builder().chatId(chatId).text("✅ Brend qabul qilindi.\n\nEndi modelini kiriting (masalan, Onix):").build();
            }

            else if (currentStep == StepEnum.ADMIN_ADDING_CAR_MODEL) {
                CarDTO carState = conversationStateService.getState(chatId);
                carState.setModel(text);
                conversationStateService.updateState(chatId, carState);
                user.setStep(StepEnum.ADMIN_ADDING_CAR_YEAR);
                userRepository.save(user);
                return SendMessage.builder().chatId(chatId).text("✅ Model qabul qilindi.\n\nEndi ishlab chiqarilgan yilini kiriting (masalan, 2024):").build();
            }

            else if (currentStep == StepEnum.ADMIN_ADDING_CAR_YEAR) {
                try {
                    int year = Integer.parseInt(text);
                    CarDTO carState = conversationStateService.getState(chatId);
                    carState.setYear(year);
                    conversationStateService.updateState(chatId, carState);
                    user.setStep(StepEnum.ADMIN_ADDING_CAR_PRICE);
                    userRepository.save(user);
                    return SendMessage.builder().chatId(chatId).text("✅ Yil qabul qilindi.\n\nEndi kunlik ijara narxini kiriting (so'mda, faqat raqam):").build();
                } catch (NumberFormatException e) {
                    return SendMessage.builder().chatId(chatId).text("❌ Xatolik! Iltimos, yilni faqat raqam bilan kiriting (masalan, 2024).").build();
                }
            }

            else if (currentStep == StepEnum.ADMIN_ADDING_CAR_PRICE) {
                try {
                    long price = Long.parseLong(text);
                    CarDTO carState = conversationStateService.getState(chatId);
                    carState.setPricePerDay(price);

                    user.setStep(StepEnum.ADMIN_MENU);
                    userRepository.save(user);

                    String confirmationText = String.format(
                            "Barcha ma'lumotlar qabul qilindi:\n\n" +
                                    "<b>Brend:</b> %s\n" +
                                    "<b>Model:</b> %s\n" +
                                    "<b>Yili:</b> %d\n" +
                                    "<b>Narxi:</b> %d so'm/kun\n\n" +
                                    "Ma'lumotlar to'g'rimi? Saqlansinmi?",
                            carState.getBrand(), carState.getModel(), carState.getYear(), carState.getPricePerDay()
                    );

                    InlineKeyboardMarkup confirmationKeyboard = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                    rows.add(List.of(
                            InlineKeyboardButton.builder().text("✅ Ha, saqlash").callbackData("admin:cars:create_confirm").build(),
                            InlineKeyboardButton.builder().text("❌ Yo'q, bekor qilish").callbackData("admin:cars:create_cancel").build()
                    ));
                    confirmationKeyboard.setKeyboard(rows);

                    return SendMessage.builder().chatId(chatId).text(confirmationText).parseMode(ParseMode.HTML).replyMarkup(confirmationKeyboard).build();

                } catch (NumberFormatException e) {
                    return SendMessage.builder().chatId(chatId).text("❌ Xatolik! Iltimos, narxni faqat raqam bilan kiriting (masalan, 350000).").build();
                }
            }
        }
        return SendMessage.builder()
                .chatId(chatId)
                .text("❌ Noto‘g‘ri buyruq!" +
                        "\nIltimos, mavjud buyruqlardan foydalaning.")
                .build();
    }
}