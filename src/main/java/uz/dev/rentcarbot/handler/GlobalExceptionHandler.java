package uz.dev.rentcarbot.handler;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import uz.dev.rentcarbot.exceptions.UserNotFoundException;

@Service
@RestControllerAdvice(basePackages = "uz.dev.rentcarbot")
public class GlobalExceptionHandler {

    @ExceptionHandler(value = UserNotFoundException.class)
    public BotApiMethod<?> handle(UserNotFoundException e) {

        return SendMessage.builder()
                .chatId(e.getChatId())
                .text(e.getMessage())
                .build();

    }

}