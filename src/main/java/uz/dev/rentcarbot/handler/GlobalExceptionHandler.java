package uz.dev.rentcarbot.handler;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import uz.dev.rentcarbot.exceptions.RestException;
import uz.dev.rentcarbot.exceptions.UserNotFoundException;
import uz.dev.rentcarbot.utils.ChatContextHolder;

import java.net.ConnectException;

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

    @ExceptionHandler(value = ConnectException.class)
    public BotApiMethod<?> handle(ConnectException e) {

        return SendMessage.builder()
                .chatId(ChatContextHolder.getChatId())
                .text("Kechirasiz server bilan aloqa yo'q iltimos keyinroq urunib ko'ring !")
                .build();

    }

    @ExceptionHandler(value = RestException.class)
    public BotApiMethod<?> handle(RestException e) {

        return SendMessage.builder()
                .chatId(e.getChatId())
                .text(e.getMessage())
                .build();

    }

}