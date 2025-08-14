package uz.dev.rentcarbot.handler;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;
import uz.dev.rentcarbot.exceptions.RestException;
import uz.dev.rentcarbot.payload.ErrorDTO;
import uz.dev.rentcarbot.utils.ChatContextHolder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class ErrorDecoderForFeign implements ErrorDecoder {
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            int status = response.status();
            Long chatId = ChatContextHolder.getChatId();

            String json = null;
            if (response.body() != null) {

                json = new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);
            }

            if (json != null && !json.isBlank()) {
                try {
                    ErrorDTO errorDTO = objectMapper.readValue(json, ErrorDTO.class);
                    return new RestException(errorDTO.getMessage(), status, chatId);
                } catch (IOException parseException) {

                    return new RestException("Unexpected error: " + json, status, chatId);
                }
            } else {

                return new RestException("No response body, status: " + status, status, chatId);
            }

        } catch (IOException e) {
            return new RuntimeException("Failed to process Feign error response", e);
        }
    }
}