package uz.dev.rentcarbot.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Created by: asrorbek
 * DateTime: 8/5/25 16:23
 **/

@Getter
public class UserNotFoundException extends RuntimeException {

    private final HttpStatus status;

    private final Long chatId;

    public UserNotFoundException(String message, HttpStatus status, Long chatId) {

        super(message);
        this.status = status;
        this.chatId = chatId;
    }

}
