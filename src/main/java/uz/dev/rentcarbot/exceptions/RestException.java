package uz.dev.rentcarbot.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Created by: asrorbek
 * DateTime: 8/5/25 16:23
 **/

@Getter
public class RestException extends RuntimeException {

    private final int status;

    private final Long chatId;

    public RestException(String message, int status, Long chatId) {

        super(message);
        this.status = status;
        this.chatId = chatId;
    }

}
