package uz.dev.rentcarbot.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import uz.dev.rentcarbot.entity.TelegramUser;
import uz.dev.rentcarbot.repository.TelegramUserRepository;
import uz.dev.rentcarbot.service.template.MessageService;
import uz.dev.rentcarbot.service.template.TextService;

/**
 * Created by: asrorbek
 * DateTime: 8/5/25 16:09
 **/

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final TextService textService;

    private final TelegramUserRepository userRepository;

    @Override
    @Transactional
    public BotApiMethod<?> processMessage(Message message) {

        Long chatId = message.getChatId();

        if (message.hasText()) {

            return textService.processText(message);

        } else if (message.hasContact()) {

            Contact contact = message.getContact();

            String phoneNumber = contact.getPhoneNumber();

            TelegramUser user = userRepository.findByChatIdOrThrowException(chatId);

            user.setPhoneNumber(phoneNumber);

            userRepository.save(user);

        }

        return null;
    }
}
