package uz.dev.rentcarbot.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.dev.rentcarbot.client.AuthClient;
import uz.dev.rentcarbot.entity.TelegramUser;
import uz.dev.rentcarbot.enums.RoleEnum;
import uz.dev.rentcarbot.enums.StepEnum;
import uz.dev.rentcarbot.payload.TokenDTO;
import uz.dev.rentcarbot.repository.TelegramUserRepository;
import uz.dev.rentcarbot.service.template.*;
import uz.dev.rentcarbot.utils.CommonUtils;

import java.util.Optional;

/**
 * Created by: asrorbek
 * DateTime: 8/5/25 16:34
 **/

@Service
public class TextServiceImpl implements TextService {

    private final TelegramUserRepository userRepository;
    private final ReplyButtonService replyButtonService;
    private final AuthClient authClient;
    private final TokenService tokenService;
    private final UserTextService userTextService;

    public TextServiceImpl(TelegramUserRepository userRepository, ReplyButtonService replyButtonService, AuthClient authClient, TokenService tokenService, UserTextService userTextService) {
        this.userRepository = userRepository;
        this.replyButtonService = replyButtonService;
        this.authClient = authClient;
        this.tokenService = tokenService;
        this.userTextService = userTextService;
    }


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

            if (user.getRole().equals(RoleEnum.USER)) {

                return userTextService.process(message);

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
}