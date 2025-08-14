package uz.dev.rentcarbot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import uz.dev.rentcarbot.entity.TelegramUser;
import uz.dev.rentcarbot.enums.RoleEnum;
import uz.dev.rentcarbot.repository.TelegramUserRepository;
import uz.dev.rentcarbot.service.template.AdminCallbackService;
import uz.dev.rentcarbot.service.template.CallbackService;
import uz.dev.rentcarbot.service.template.UserCallbackService;
import uz.dev.rentcarbot.utils.ChatContextHolder;

/**
 * Created by: asrorbek
 * DateTime: 8/8/25 18:26
 **/

@Service
public class CallbackServiceImpl implements CallbackService {

    private final TelegramUserRepository telegramUserRepository;
    private final UserCallbackService userCallbackService;
    private final AdminCallbackService adminCallbackService;

    public CallbackServiceImpl(TelegramUserRepository telegramUserRepository, UserCallbackService userCallbackService, AdminCallbackService adminCallbackService) {
        this.telegramUserRepository = telegramUserRepository;
        this.userCallbackService = userCallbackService;
        this.adminCallbackService = adminCallbackService;
    }

    @Override
    public BotApiMethod<?> processCallbackQuery(CallbackQuery callbackQuery) {

        Long chatId = callbackQuery.getFrom().getId();

        ChatContextHolder.setChatId(chatId);

        TelegramUser user = telegramUserRepository.findByChatIdOrThrowException(chatId);

        if (user.getRole().equals(RoleEnum.USER)) {

            return userCallbackService.process(callbackQuery);

        } else if(user.getRole().equals(RoleEnum.ADMIN)) {

            return adminCallbackService.process(callbackQuery);

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