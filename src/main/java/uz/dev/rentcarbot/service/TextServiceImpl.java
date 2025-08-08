package uz.dev.rentcarbot.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.dev.rentcarbot.entity.TelegramUser;
import uz.dev.rentcarbot.enums.RoleEnum;
import uz.dev.rentcarbot.enums.StepEnum;
import uz.dev.rentcarbot.repository.TelegramUserRepository;
import uz.dev.rentcarbot.service.template.ReplyButtonService;
import uz.dev.rentcarbot.service.template.TextService;
import uz.dev.rentcarbot.utils.CommonUtils;

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
                                    \uD83C\uDF89 **Xush kelibsiz, @RentCarBot ga!** \uD83D\uDE97 \s
                                    Ro‘yxatdan o‘tish uchun **telefon raqamingizni** yuboring. \s
                                    Bu sizning ijaralaringizni boshqarish va xizmatlarimizdan foydalanish uchun zarur!""")
                            .replyMarkup(buttonMarkup)
                            .parseMode("MarkdownV2")
                            .build();

                } else {

                    TelegramUser user = userOptional.get();



                }
            }

        }

        return null;

    }
}
