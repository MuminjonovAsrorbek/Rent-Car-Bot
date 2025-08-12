package uz.dev.rentcarbot.service;

import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.dev.rentcarbot.client.AuthClient;
import uz.dev.rentcarbot.client.CarClient;
import uz.dev.rentcarbot.client.OfficeClient;
import uz.dev.rentcarbot.client.PromoCodeClient;
import uz.dev.rentcarbot.config.MyTelegramBot;
import uz.dev.rentcarbot.entity.TelegramUser;
import uz.dev.rentcarbot.enums.RoleEnum;
import uz.dev.rentcarbot.enums.StepEnum;
import uz.dev.rentcarbot.payload.*;
import uz.dev.rentcarbot.repository.TelegramUserRepository;
import uz.dev.rentcarbot.service.template.InlineButtonService;
import uz.dev.rentcarbot.service.template.ReplyButtonService;
import uz.dev.rentcarbot.service.template.TextService;
import uz.dev.rentcarbot.service.template.TokenService;
import uz.dev.rentcarbot.utils.CommonUtils;
import uz.dev.rentcarbot.utils.DateTimeValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Created by: asrorbek
 * DateTime: 8/5/25 16:34
 **/

@Service
public class TextServiceImpl implements TextService {

    private final TelegramUserRepository userRepository;

    private final ReplyButtonService replyButtonService;

    private final InlineButtonService inlineButtonService;

    private final AuthClient authClient;

    private final TokenService tokenService;

    private final MyTelegramBot telegramBot;
    private final OfficeClient officeClient;
    private final CarClient carClient;
    private final PromoCodeClient promoCodeClient;

    public TextServiceImpl(TelegramUserRepository userRepository, ReplyButtonService replyButtonService, InlineButtonService inlineButtonService, AuthClient authClient, TokenService tokenService, @Lazy MyTelegramBot myTelegramBot, OfficeClient officeClient, CarClient carClient, PromoCodeClient promoCodeClient) {
        this.userRepository = userRepository;
        this.replyButtonService = replyButtonService;
        this.inlineButtonService = inlineButtonService;
        this.authClient = authClient;
        this.tokenService = tokenService;
        this.telegramBot = myTelegramBot;
        this.officeClient = officeClient;
        this.carClient = carClient;
        this.promoCodeClient = promoCodeClient;
    }

    @Override
    @Transactional
    public BotApiMethod<?> processText(Message message) {

        String text = message.getText();

        Long chatId = message.getChatId();

        Integer messageId = message.getMessageId();

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

                    return SendMessage.builder()
                            .chatId(chatId)
                            .text("""
                                    🎉 Xush kelibsiz, @RentCarBot ga!
                                    Ro‘yxatdan o‘tish uchun telefon raqamingizni yuboring.
                                    Bu sizning ijaralaringizni boshqarish va xizmatlarimizdan foydalanish uchun zarur!
                                    """)
                            .replyMarkup(buttonMarkup)
                            .build();

                } else {

                    TelegramUser user = userOptional.get();

                    TokenDTO tokenDTO = authClient.getTokenByPhoneNumber(user.getPhoneNumber());

                    tokenService.saveTokens(chatId, tokenDTO);

                    ReplyKeyboardMarkup replyKeyboardMarkup = replyButtonService.buildMenuButtons(user.getRole());

                    user.setStep(StepEnum.SELECT_MENU);

                    userRepository.save(user);

                    return SendMessage.builder()
                            .chatId(user.getChatId())
                            .text("Menu")
                            .replyMarkup(replyKeyboardMarkup)
                            .build();

                }
            }

        } else {

            TelegramUser user = userRepository.findByChatIdOrThrowException(chatId);

            if (user.getStep().equals(StepEnum.SELECT_MENU)) {

                if (text.equals("\uD83D\uDD11 Ijaraga olish")) {

                    return SendMessage.builder()
                            .chatId(chatId)
                            .text("""
                                    <b>\uD83D\uDE97 Ijaraga olish uchun mavjud mashinalar:</b>
                                    <i>Quyidagi tugmani bosing va hozirda band bo‘lmagan avtomobillar ro‘yxatini ko‘ring.</i>
                                    \s""")
                            .parseMode(ParseMode.HTML)
                            .replyMarkup(inlineButtonService.buildAvailableCars())
                            .build();

                } else if (text.equals("⚖️ Jarimalar")) {

                    return SendMessage.builder()
                            .chatId(chatId)
                            .text("""
                                    <b>⚖️ Jarimalar bo‘limi</b>
                                    
                                    Siz bu bo‘limda quyidagi ma’lumotlarni ko‘rishingiz mumkin: \s
                                    \uD83C\uDD95 <b>Yangi jarimalar</b> — Hozirgi va to‘lanmagan jarimalaringiz ro‘yxati. \s
                                    \uD83D\uDCDC <b>Barcha jarimalar</b> — Eski va yangi barcha jarimalaringiz tarixi.
                                    
                                    <i>Jarimalaringizni vaqtida to‘lab, muammolardan qoching \uD83D\uDE09</i>
                                    """)
                            .parseMode(ParseMode.HTML)
                            .replyMarkup(inlineButtonService.buildPenaltyMenu())
                            .build();

                }

            } else if (user.getStep().equals(StepEnum.PICKUP_DATE)) {

                return pickupDate(text, user, chatId);

            } else if (user.getStep().equals(StepEnum.RETURN_DATE)) {

                return returnDate(text, user, chatId);

            } else if (user.getStep().equals(StepEnum.PAYMENT_METHOD)) {

                if (text.equals("Orqaga")) {

                    user.setStep(StepEnum.SELECT_MENU);

                    userRepository.save(user);

                    telegramBot.getUserBookings().remove(chatId);

                    return SendMessage.builder()
                            .chatId(chatId)
                            .text("MENU")
                            .build();

                }

            } else if (user.getStep().equals(StepEnum.SEND_PROMO_CODE)) {

                return sendPromoCode(chatId, text, user);

            } else if (user.getStep().equals(StepEnum.RECIPIENT_FULL_NAME)) {

                telegramBot.getUserBookings().get(chatId).setRecipientFullName(text);

                user.setStep(StepEnum.RECIPIENT_PHONE);

                userRepository.save(user);

                return SendMessage.builder()
                        .chatId(chatId)
                        .text("Qabul qiluvchining telefon raqamini kiriting :")
                        .build();

            } else if (user.getStep().equals(StepEnum.RECIPIENT_PHONE)) {

                Pattern regex = Pattern.compile("^\\+998\\d{9}$");

                if (regex.matcher(text).matches()) {

                    telegramBot.getUserBookings().get(chatId).setRecipientPhone(text);

                    PageableDTO<OfficeDTO> dtos = officeClient.getAllOffices(0, 10);

                    List<OfficeDTO> allOffices = dtos.getObjects();

                    StringBuilder sb = new StringBuilder();

                    sb.append("<b>\uD83D\uDCCD Qaysi ofisimizdan olib ketasiz?</b>").append("\n\n");

                    for (int i = 0; i < allOffices.size(); i++) {

                        sb.append(i + 1).append(" . ").append("<b>").append(allOffices.get(i).getName()).append("</b>").append("\n");
                        sb.append("Manzil: ").append(allOffices.get(i).getAddress()).append("\n\n");

                    }

                    user.setStep(StepEnum.PICKUP_OFFICE);

                    userRepository.save(user);

                    return SendMessage.builder()
                            .chatId(chatId)
                            .text(sb.toString())
                            .parseMode(ParseMode.HTML)
                            .replyMarkup(inlineButtonService.buildOffices(dtos))
                            .build();
                } else {

                    return SendMessage.builder()
                            .chatId(chatId)
                            .text("Telefon raqami formati notog'ri . Misol : +998912345678")
                            .build();

                }

            }

        }

        return SendMessage.builder()
                .chatId(chatId)
                .text("""
                        ❌ Noto‘g‘ri buyruq!
                        Iltimos, mavjud komandalarni ishlating.
                        """)
                .build();

    }

    @Transactional
    public SendMessage sendPromoCode(Long chatId, String text, TelegramUser user) {

        BookingCreateDTO dto = telegramBot.getUserBookings().get(chatId);

        if (text.equals("Orqaga")) {

            user.setStep(StepEnum.CHECKED_BOOKING);

            userRepository.save(user);

            return checkedBooking(dto, chatId);

        } else {

            boolean exists = promoCodeClient.codeValidate(text);

            if (exists) {

                dto.setPromoCode(text);

                user.setStep(StepEnum.CHECKED_BOOKING);

                userRepository.save(user);

                return checkedBooking(dto, chatId);

            } else {

                return SendMessage.builder()
                        .chatId(chatId)
                        .text("Siz kiritgan " + text + " promo-code topilmadi !")
                        .build();

            }

        }
    }

    @Override
    public SendMessage checkedBooking(BookingCreateDTO dto, Long chatId) {

        CarDTO car = carClient.getCarById(dto.getCarId());

        OfficeDTO pickupOffice = officeClient.getOfficeById(dto.getPickupOfficeId());

        OfficeDTO returnOffice = officeClient.getOfficeById(dto.getPickupOfficeId());

        StringBuilder sb = new StringBuilder();

        sb.append("<b>📋 Bron ma'lumotlari</b>\n\n");

        sb.append("<b>🚗 Avtomobil:</b> ")
                .append(car.getBrand()).append(" ")
                .append(car.getModel())
                .append(" (").append(car.getSeats()).append(" o‘rinli, ")
                .append(car.getFuelType()).append(", ")
                .append(car.getTransmission()).append(")\n\n");

        sb.append("<b>📍 Olish manzili:</b> ")
                .append(pickupOffice.getName())
                .append(" — ").append(pickupOffice.getAddress()).append("\n");

        sb.append("<b>📍 Qaytarish manzili:</b> ")
                .append(returnOffice.getName())
                .append(" — ").append(returnOffice.getAddress()).append("\n\n");

        sb.append("<b>📅 Olish vaqti:</b> ").append(dto.getPickupDate()).append("\n");
        sb.append("<b>📅 Qaytarish vaqti:</b> ").append(dto.getReturnDate()).append("\n\n");

        sb.append("<b>💳 To‘lov turi:</b> ").append(dto.getPaymentMethod()).append("\n");

        if (dto.isForSelf()) {
            sb.append("<b>👤 Kim uchun:</b> O‘zi uchun\n");
        } else {
            sb.append("<b>👤 Qabul qiluvchi:</b> ").append(dto.getRecipientFullName()).append("\n");
            sb.append("<b>📞 Telefon:</b> ").append(dto.getRecipientPhone()).append("\n");
        }

        if (dto.getPromoCode() != null && !dto.getPromoCode().isBlank()) {
            sb.append("<b>🎟 Promo kod:</b> ").append(dto.getPromoCode()).append("\n");
        }

        sb.append("\n<b>✅ Ma'lumotlar to‘g‘rimi?</b>");

        return SendMessage.builder()
                .chatId(chatId)
                .text(sb.toString())
                .parseMode(ParseMode.HTML)
                .replyMarkup(inlineButtonService.buildYesOrNo("booking"))
                .build();
    }

    private SendMessage returnDate(String text, TelegramUser user, Long chatId) {

        if (text.equals("Orqaga")) {

            user.setStep(StepEnum.SELECT_MENU);

            userRepository.save(user);

            telegramBot.getUserBookings().remove(chatId);

            return SendMessage.builder()
                    .chatId(chatId)
                    .text("MENU")
                    .replyMarkup(replyButtonService.buildMenuButtons(user.getRole()))
                    .build();

        }

        Optional<LocalDateTime> optionalTime = DateTimeValidator.validDateTime(text);

        if (optionalTime.isEmpty()) {

            return SendMessage.builder()
                    .chatId(chatId)
                    .text("⛔ Faqat shu formatdagi sana va vaqt qabul qilinadi.  \n" +
                            "✅ Format: <b>YYYY-MM-DD HH:mm</b>  ")
                    .replyMarkup(replyButtonService.buildCancelButton())
                    .build();

        }

        LocalDateTime localDateTime = optionalTime.get();

        telegramBot.getUserBookings().get(chatId).setReturnDate(localDateTime);

        user.setStep(StepEnum.PAYMENT_METHOD);

        userRepository.save(user);

        return SendMessage.builder()
                .chatId(chatId)
                .text("To'lov usulini tanlang :")
                .replyMarkup(inlineButtonService.buildPaymentMethod())
                .build();
    }

    private SendMessage pickupDate(String text, TelegramUser user, Long chatId) {

        if (text.equals("Orqaga")) {

            user.setStep(StepEnum.SELECT_MENU);

            userRepository.save(user);

            telegramBot.getUserBookings().remove(chatId);

            return SendMessage.builder()
                    .chatId(chatId)
                    .text("MENU")
                    .replyMarkup(replyButtonService.buildMenuButtons(user.getRole()))
                    .build();

        }

        Optional<LocalDateTime> optionalTime = DateTimeValidator.validDateTime(text);

        if (optionalTime.isEmpty()) {

            return SendMessage.builder()
                    .chatId(chatId)
                    .text("⛔ Faqat shu formatdagi sana va vaqt qabul qilinadi.  \n" +
                            "✅ Format: <b>YYYY-MM-DD HH:mm</b>  ")
                    .replyMarkup(replyButtonService.buildCancelButton())
                    .build();

        }

        LocalDateTime localDateTime = optionalTime.get();

        telegramBot.getUserBookings().get(chatId).setPickupDate(localDateTime);

        PageableDTO<OfficeDTO> dtos = officeClient.getAllOffices(0, 10);

        List<OfficeDTO> allOffices = dtos.getObjects();

        StringBuilder message = new StringBuilder();

        message.append("<b>\uD83D\uDCCD Qaysi ofisimizga qaytarasiz?</b>").append("\n\n");

        for (int i = 0; i < allOffices.size(); i++) {

            message.append(i + 1).append(" . ").append("<b>").append(allOffices.get(i).getName()).append("</b>").append("\n");
            message.append("Manzil: ").append(allOffices.get(i).getAddress()).append("\n\n");

        }
        user.setStep(StepEnum.RETURN_OFFICE);

        userRepository.save(user);

        return SendMessage.builder()
                .chatId(chatId)
                .text(message.toString())
                .parseMode(ParseMode.HTML)
                .replyMarkup(inlineButtonService.buildOffices(dtos))
                .build();

    }
}