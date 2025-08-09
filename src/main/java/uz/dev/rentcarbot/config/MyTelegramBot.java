package uz.dev.rentcarbot.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.dev.rentcarbot.service.template.UpdateDispatcherService;

/**
 * Created by: asrorbek
 * DateTime: 8/5/25 15:00
 **/

@Component
@RequiredArgsConstructor
public class MyTelegramBot extends TelegramWebhookBot {

    @Value("${telegram.bots.bots-list.bot.username}")
    private String botUsername;

    @Value("${telegram.bots.bots-list.bot.token}")
    private String botToken;

    @Value("${telegram.bots.bots-list.bot.path}")
    private String webhookPath;

    private final UpdateDispatcherService service;

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {

        return service.updateDispatch(update);

    }

    public Message sendPhoto(SendPhoto sendPhoto) {
        try {

            return execute(sendPhoto);

        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }

    public void deleteMessage(DeleteMessage deleteMessage){

        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotPath() {
        return webhookPath;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }
}
