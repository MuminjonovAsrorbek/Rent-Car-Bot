package uz.dev.rentcarbot.service;

import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import uz.dev.rentcarbot.config.MyTelegramBot;
import uz.dev.rentcarbot.entity.TelegramUser;
import uz.dev.rentcarbot.enums.StepEnum;
import uz.dev.rentcarbot.repository.TelegramUserRepository;
import uz.dev.rentcarbot.service.template.AdminCallbackService;
import uz.dev.rentcarbot.service.template.ReplyButtonService;
import uz.dev.rentcarbot.utils.ChatContextHolder;

/**
 * Created by: asrorbek
 * DateTime: 8/13/25 21:40
 **/

@Service
public class AdminCallbackServiceImpl implements AdminCallbackService {

    private final TelegramUserRepository telegramUserRepository;
    private final MyTelegramBot myTelegramBot;
    private final ReplyButtonService replyButtonService;

    public AdminCallbackServiceImpl(TelegramUserRepository telegramUserRepository, @Lazy MyTelegramBot myTelegramBot, ReplyButtonService replyButtonService) {
        this.telegramUserRepository = telegramUserRepository;
        this.myTelegramBot = myTelegramBot;
        this.replyButtonService = replyButtonService;
    }

    @Override
    @Transactional
    public BotApiMethod<?> process(CallbackQuery callbackQuery) {

        String data = callbackQuery.getData();
        Long chatId = callbackQuery.getFrom().getId();
        ChatContextHolder.setChatId(chatId);
        Integer messageId = callbackQuery.getMessage().getMessageId();
        String callbackId = callbackQuery.getId();

        TelegramUser user = telegramUserRepository.findByChatIdOrThrowException(chatId);

        if (user.getStep().equals(StepEnum.SELECT_MENU_ADMIN)) {

            if (data.startsWith("booking-")) {

                if (data.equals("booking-confirm")) {

                    String senMessage = "✍️ Tasdiqlash uchun buyurtma ID raqamini kiriting:\n" +
                            "(Masalan: 123)";

                    user.setStep(StepEnum.BOOKING_CONFIRM);

                    telegramUserRepository.save(user);

                    DeleteMessage deleteMessage = DeleteMessage.builder()
                            .chatId(chatId)
                            .messageId(messageId)
                            .build();

                    myTelegramBot.deleteMessage(deleteMessage);

                    return SendMessage.builder()
                            .chatId(chatId)
                            .text(senMessage)
                            .replyMarkup(replyButtonService.buildCancelButton())
                            .build();

                } else if (data.equals("booking-complete")) {

                    String senMessage = "✍️ Tugatish uchun buyurtma ID raqamini kiriting:\n" +
                            "(Masalan: 123)";

                    user.setStep(StepEnum.BOOKING_COMPLETE);

                    telegramUserRepository.save(user);

                    DeleteMessage deleteMessage = DeleteMessage.builder()
                            .chatId(chatId)
                            .messageId(messageId)
                            .build();

                    myTelegramBot.deleteMessage(deleteMessage);

                    return SendMessage.builder()
                            .chatId(chatId)
                            .text(senMessage)
                            .replyMarkup(replyButtonService.buildCancelButton())
                            .build();

                } else if (data.equals("booking-cancel")) {

                    String senMessage = "✍️ Bekor qilish uchun buyurtma ID raqamini kiriting:\n" +
                            "(Masalan: 123)";

                    user.setStep(StepEnum.BOOKING_CANCEL);

                    telegramUserRepository.save(user);

                    DeleteMessage deleteMessage = DeleteMessage.builder()
                            .chatId(chatId)
                            .messageId(messageId)
                            .build();

                    myTelegramBot.deleteMessage(deleteMessage);

                    return SendMessage.builder()
                            .chatId(chatId)
                            .text(senMessage)
                            .replyMarkup(replyButtonService.buildCancelButton())
                            .build();

                }

            } else if (data.startsWith("payment-")) {

                if (data.equals("payment-confirm")) {

                    String senMessage = "✍️ To'lovni tasdiqlash uchun buyurtma ID raqamini kiriting:\n" +
                            "(Masalan: 123)";

                    user.setStep(StepEnum.PAYMENT_CONFIRM);

                    telegramUserRepository.save(user);

                    DeleteMessage deleteMessage = DeleteMessage.builder()
                            .chatId(chatId)
                            .messageId(messageId)
                            .build();

                    myTelegramBot.deleteMessage(deleteMessage);

                    return SendMessage.builder()
                            .chatId(chatId)
                            .text(senMessage)
                            .replyMarkup(replyButtonService.buildCancelButton())
                            .build();

                } else {

                    String senMessage = "✍️ To'lovni bekor qilish uchun buyurtma ID raqamini kiriting:\n" +
                            "(Masalan: 123)";

                    user.setStep(StepEnum.PAYMENT_CANCEL);

                    telegramUserRepository.save(user);

                    DeleteMessage deleteMessage = DeleteMessage.builder()
                            .chatId(chatId)
                            .messageId(messageId)
                            .build();

                    myTelegramBot.deleteMessage(deleteMessage);

                    return SendMessage.builder()
                            .chatId(chatId)
                            .text(senMessage)
                            .replyMarkup(replyButtonService.buildCancelButton())
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
}
