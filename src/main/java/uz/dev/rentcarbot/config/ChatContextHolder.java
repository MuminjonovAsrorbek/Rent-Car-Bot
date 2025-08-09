package uz.dev.rentcarbot.config;

import org.springframework.stereotype.Component;

@Component
public class ChatContextHolder {
    private static final ThreadLocal<Long> chatIdHolder = new ThreadLocal<>();

    public static void setChatId(Long chatId) {
        chatIdHolder.set(chatId);
    }

    public static Long getChatId() {
        return chatIdHolder.get();
    }

    public static void clear() {
        chatIdHolder.remove();
    }
}
