package uz.dev.rentcarbot.service.template;

import uz.dev.rentcarbot.model.UserToken;

/**
 * Created by: asrorbek
 * DateTime: 8/8/25 16:28
 **/

public interface TokenService {
    void saveTokens(String chatId, String accessToken, String refreshToken);

    UserToken getTokens(String chatId);

    void deleteTokens(String chatId);
}
