package uz.dev.rentcarbot.service;

import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
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

                switch (data) {
                    case "booking-confirm" -> {

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

                    }
                    case "booking-complete" -> {

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

                    }
                    case "booking-cancel" -> {

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
                }

            } else if (data.startsWith("payment-")) {

                String senMessage;

                if (data.equals("payment-confirm")) {

                    senMessage = "✍️ To'lovni tasdiqlash uchun buyurtma ID raqamini kiriting:\n" +
                            "(Masalan: 123)";

                    user.setStep(StepEnum.PAYMENT_CONFIRM);

                    telegramUserRepository.save(user);

                    DeleteMessage deleteMessage = DeleteMessage.builder()
                            .chatId(chatId)
                            .messageId(messageId)
                            .build();

                    myTelegramBot.deleteMessage(deleteMessage);

                } else {

                    senMessage = "✍️ To'lovni bekor qilish uchun buyurtma ID raqamini kiriting:\n" +
                            "(Masalan: 123)";

                    user.setStep(StepEnum.PAYMENT_CANCEL);

                    telegramUserRepository.save(user);

                    DeleteMessage deleteMessage = DeleteMessage.builder()
                            .chatId(chatId)
                            .messageId(messageId)
                            .build();

                    myTelegramBot.deleteMessage(deleteMessage);

                }
                return SendMessage.builder()
                        .chatId(chatId)
                        .text(senMessage)
                        .replyMarkup(replyButtonService.buildCancelButton())
                        .build();

            } else if (data.startsWith("penalty-")) {

                if (data.startsWith("penalty-booking")) {

                    String senMessage;

                    if (data.equals("penalty-booking-confirm")) {

                        senMessage = "✍️ Jarimani tasdiqlash uchun buyurtma ID raqamini kiriting:\n" +
                                "(Masalan: 123)";

                        user.setStep(StepEnum.PENALTY_BOOKING_CONFIRM);

                        telegramUserRepository.save(user);

                        DeleteMessage deleteMessage = DeleteMessage.builder()
                                .chatId(chatId)
                                .messageId(messageId)
                                .build();

                        myTelegramBot.deleteMessage(deleteMessage);

                    } else {

                        senMessage = "✍️ Jarimani bekor qilish uchun buyurtma ID raqamini kiriting:\n" +
                                "(Masalan: 123)";

                        user.setStep(StepEnum.PENALTY_BOOKING_CANCEL);

                        telegramUserRepository.save(user);

                        DeleteMessage deleteMessage = DeleteMessage.builder()
                                .chatId(chatId)
                                .messageId(messageId)
                                .build();

                        myTelegramBot.deleteMessage(deleteMessage);

                    }
                    return SendMessage.builder()
                            .chatId(chatId)
                            .text(senMessage)
                            .replyMarkup(replyButtonService.buildCancelButton())
                            .build();

                } else {

                    String senMessage;

                    if (data.equals("penalty-confirm")) {

                        senMessage = "✍️ Jarimani tasdiqlash uchun penalty ID raqamini kiriting:\n" +
                                "(Masalan: 123)";

                        user.setStep(StepEnum.PENALTY_CONFIRM);

                        telegramUserRepository.save(user);

                        DeleteMessage deleteMessage = DeleteMessage.builder()
                                .chatId(chatId)
                                .messageId(messageId)
                                .build();

                        myTelegramBot.deleteMessage(deleteMessage);

                    } else {

                        senMessage = "✍️ Jarimani bekor qilish uchun penalty ID raqamini kiriting:\n" +
                                "(Masalan: 123)";

                        user.setStep(StepEnum.PENALTY_CANCEL);

                        telegramUserRepository.save(user);

                        DeleteMessage deleteMessage = DeleteMessage.builder()
                                .chatId(chatId)
                                .messageId(messageId)
                                .build();

                        myTelegramBot.deleteMessage(deleteMessage);

                    }
                    return SendMessage.builder()
                            .chatId(chatId)
                            .text(senMessage)
                            .replyMarkup(replyButtonService.buildCancelButton())
                            .build();

                }

            } else if (data.startsWith("ANNOUNCE")) {

                if (data.equals("ANNOUNCE_ALL")) {

                    user.setStep(StepEnum.SEND_MSG_ALL);

                    telegramUserRepository.save(user);

                    DeleteMessage deleteMessage = DeleteMessage.builder()
                            .chatId(chatId)
                            .messageId(messageId)
                            .build();

                    myTelegramBot.deleteMessage(deleteMessage);

                    return SendMessage.builder()
                            .chatId(chatId)
                            .text("Habaringizni yuboring")
                            .replyMarkup(replyButtonService.buildCancelButton())
                            .build();

                } else {

                    user.setStep(StepEnum.SEND_MSG_ONE);

                    telegramUserRepository.save(user);

                    DeleteMessage deleteMessage = DeleteMessage.builder()
                            .chatId(chatId)
                            .messageId(messageId)
                            .build();

                    myTelegramBot.deleteMessage(deleteMessage);

                    return SendMessage.builder()
                            .chatId(chatId)
                            .text("Iltimos user chatId kiriting")
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
