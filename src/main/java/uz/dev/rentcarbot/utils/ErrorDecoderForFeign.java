package uz.dev.rentcarbot.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;
import uz.dev.rentcarbot.exceptions.RestException;
import uz.dev.rentcarbot.payload.ErrorDTO;

import java.io.IOException;

/**
 * Created by: Mehrojbek
 * DateTime: 10/07/25 19:53
 **/
@Component
public class ErrorDecoderForFeign implements ErrorDecoder {
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);

    @Override
    public Exception decode(String s, Response response) {
        try {

            int status = response.status();

            String json = new String(response.body().asInputStream().readAllBytes());

            Long chatId = ChatContextHolder.getChatId();

            ErrorDTO errorDTO = objectMapper.readValue(json, ErrorDTO.class);

            throw  new RestException(errorDTO.getMessage(), status, chatId);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}