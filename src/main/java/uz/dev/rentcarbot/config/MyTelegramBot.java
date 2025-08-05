package uz.dev.rentcarbot.config;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Created by: asrorbek
 * DateTime: 8/5/25 15:00
 **/

@Component
@AllArgsConstructor
@NoArgsConstructor
public class MyTelegramBot extends TelegramWebhookBot {

    @Value("${telegram.bots.bots-list.bot.username}")
    private String botUsername;

    @Value("${telegram.bots.bots-list.bot.token}")
    private String botToken;

    @Value("${telegram.bots.bots-list.bot.path}")
    private String webhookPath;

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            SendMessage response = new SendMessage();
            response.setChatId(chatId);

            if (messageText.equals("/start")) {
                response.setText("Xush kelibsiz, @testerofnull_bot ga! 🚗 Mashina ijarasi xizmatlarimiz bilan tanishing!");
            } else {
                response.setText("Siz yozdingiz: " + messageText);
            }

            return response;
        }
        return null;
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
