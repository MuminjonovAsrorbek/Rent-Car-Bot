package uz.dev.rentcarbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.dev.rentcarbot.client.ReviewClient;
import uz.dev.rentcarbot.entity.TelegramUser;
import uz.dev.rentcarbot.exceptions.UserNotFoundException;
import uz.dev.rentcarbot.payload.PageableDTO;
import uz.dev.rentcarbot.payload.ReviewDTO;
import uz.dev.rentcarbot.repository.TelegramUserRepository;
import uz.dev.rentcarbot.service.template.ReviewBotService;

@Component
@RequiredArgsConstructor
public class ReviewTelegramBot extends TelegramLongPollingBot {

    private final ReviewBotService reviewBotService;
    private final ReviewClient reviewClient;
    private final TelegramUserRepository telegramUserRepository;

    @Value("${telegram.bots.bots-list.bot.username}")
    private String botUsername;

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {

            Message message = update.getMessage();
            String text = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            if (text.startsWith("get_review")) {
                handleGetReview(chatId, text);
            } else if (text.startsWith("/add_review")) {
                handleAddReview(chatId, text);
            } else if (text.startsWith("/delete_review")) {
                handleDeleteReview(chatId, text);
            } else {
                sendMessage(chatId, "Buyruqlar:\n" +
                        "/add_review {carId} {rating} {comment}\n" +
                        "/delete_review {reviewId}");
            }

        }

    }

    private void handleGetReview(Long chatId, String text) {

        String[] parts = text.split(" ");
        if (parts.length < 2) {
            sendMessage(chatId, "Format xato!\\nMisol: /get_reviews 1");
            return;
        }

        Long carId = Long.parseLong(parts[1]);

        String response = reviewBotService.listReviews(carId, 0, 5);

        sendMessage(chatId, response);
    }

    private void handleAddReview(Long chatId, String text) {

        String[] parts = text.split(" ", 4);
        if (parts.length < 4) {
            sendMessage(chatId, "Format: /add_review <carId> <rating> <comment>");
            return;
        }

        Long carId = Long.parseLong(parts[1]);
        int rating = Integer.parseInt(parts[2]);
        String comment = parts[3];

        Long userId = getUserIdFromChatId(chatId);

        String response = reviewBotService.createReview(carId, rating, comment, userId);

        sendMessage(chatId, response);
    }

    private void handleDeleteReview(Long chatId, String text) {

        String[] parts = text.split(" ");
        if (parts.length < 2) {
            sendMessage(chatId, "Format: /delete_review <reviewId>");
            return;
        }

        Long reviewId = Long.parseLong(parts[1]);
        Long userId = getUserIdFromChatId(chatId);

        String response = reviewBotService.deleteReview(reviewId, userId);

        sendMessage(chatId, response);
    }

    private void sendMessage(Long chatId, String text) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);

        try {
            execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Long getUserIdFromChatId(Long chatId) {
        return telegramUserRepository.findByChatId(chatId)
                .map(TelegramUser::getUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found", HttpStatus.NOT_FOUND, chatId));
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

}
