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

            Boolean registered = userClient.isRegistered(phoneNumber);

            TelegramUser user = userRepository.findByChatIdOrThrowException(chatId);

            if (registered) {

                user.setPhoneNumber(phoneNumber);

                TelegramUser saved = userRepository.save(user);

                TokenDTO tokenDTO = authClient.getTokenByPhoneNumber(phoneNumber);

                tokenService.saveTokens(chatId, tokenDTO);

                ReplyKeyboardMarkup replyKeyboardMarkup = replyButtonService.buildMenuButtons(saved.getRole());

                return SendMessage.builder()
                        .chatId(saved.getChatId())
                        .text("Menu")
                        .replyMarkup(replyKeyboardMarkup)
                        .build();


            } else {

                return SendMessage.builder()
                        .chatId(chatId)
                        .text("""
                                ✋ *Assalomu alaykum!*
                                Siz hali RentCar tizimida ro'yxatdan o'tmagansiz.
                                
                                \uD83D\uDCCC Xizmatlarimizdan foydalanish uchun iltimos, avval tizimimizda ro'yxatdan o'ting:
                                
                                ➡️ [Ro'yxatdan o'tish uchun bu yerga bosing](http://localhost:8080/swagger-ui/index.html)
                                
                                ✅ Ro'yxatdan o'tganingizdan so'ng /start buyrug'ii bosing, ushbu bot orqali mashina bron qilish, buyurtmalarni kuzatish va boshqa qulay funksiyalar sizga taqdim etiladi.
                                """)
                        .build();

            }


        }

        return null;
    }
}
