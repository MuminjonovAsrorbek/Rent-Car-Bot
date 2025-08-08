package uz.dev.rentcarbot.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.dev.rentcarbot.client.AuthClient;
import uz.dev.rentcarbot.client.UserClient;
import uz.dev.rentcarbot.entity.TelegramUser;
import uz.dev.rentcarbot.enums.StepEnum;
import uz.dev.rentcarbot.payload.TgUserDTO;
import uz.dev.rentcarbot.payload.TokenDTO;
import uz.dev.rentcarbot.repository.TelegramUserRepository;
import uz.dev.rentcarbot.service.template.MessageService;
import uz.dev.rentcarbot.service.template.ReplyButtonService;
import uz.dev.rentcarbot.service.template.TextService;
import uz.dev.rentcarbot.service.template.TokenService;

/**
 * Created by: asrorbek
 * DateTime: 8/5/25 16:09
 **/

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final TextService textService;

    private final TelegramUserRepository userRepository;

    private final UserClient userClient;

    private final AuthClient authClient;

    private final TokenService tokenService;

    private final ReplyButtonService replyButtonService;

    @Override
    @Transactional
    public BotApiMethod<?> processMessage(Message message) {

        Long chatId = message.getChatId();

        if (message.hasText()) {

            return textService.processText(message);

        } else if (message.hasContact()) {

            Contact contact = message.getContact();

            String phoneNumber = contact.getPhoneNumber();

            TgUserDTO registered = userClient.isRegistered(phoneNumber);

            TelegramUser user = userRepository.findByChatIdOrThrowException(chatId);

            if (registered.getActive()) {

                user.setPhoneNumber(phoneNumber);

                TokenDTO tokenDTO = authClient.getTokenByPhoneNumber(phoneNumber);

                tokenService.saveTokens(chatId, tokenDTO);

                ReplyKeyboardMarkup replyKeyboardMarkup = replyButtonService.buildMenuButtons(user.getRole());

                user.setStep(StepEnum.SELECT_MENU);

                userRepository.save(user);

                return SendMessage.builder()
                        .chatId(user.getChatId())
                        .text("Menu")
                        .replyMarkup(replyKeyboardMarkup)
                        .build();


            } else {

                userRepository.delete(user);

                return SendMessage.builder()
                        .chatId(chatId)
                        .text("""
                                ✋ <b>Assalomu alaykum!</b>
                                
                                Siz hali RentCar tizimida ro'yxatdan o'tmagansiz.
                                
                                📌 Xizmatlarimizdan foydalanish uchun iltimos, avval tizimimizda ro'yxatdan o'ting:
                                
                                ➡️ <a href="http://192.168.100.61:8080/swagger-ui/index.html">Ro'yxatdan o'tish</a>
                                
                                ✅ Ro'yxatdan o'tgach <b>/start</b> buyrug'ini bosing.
                                """)
                        .parseMode("HTML")
                        .build();

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
