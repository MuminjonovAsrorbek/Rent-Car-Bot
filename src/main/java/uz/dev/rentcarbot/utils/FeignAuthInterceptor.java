package uz.dev.rentcarbot.utils;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uz.dev.rentcarbot.service.template.TokenService;

@Component
@RequiredArgsConstructor
public class FeignAuthInterceptor implements RequestInterceptor {

    private final TokenService tokenService;

    @Override
    public void apply(RequestTemplate template) {

        String url = template.url();

        if (url.startsWith("/open/telegram")) {
            return;
        }

        Long chatId = ChatContextHolder.getChatId();

        String accessToken = tokenService.getAccessToken(chatId);

        if (accessToken != null)
            template.header("Authorization", "Bearer " + accessToken);
    }
}
