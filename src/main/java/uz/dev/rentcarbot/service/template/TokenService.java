package uz.dev.rentcarbot.service.template;

import uz.dev.rentcarbot.payload.TokenDTO;

/**
 * Created by: asrorbek
 * DateTime: 8/8/25 16:28
 **/

public interface TokenService {

    void saveTokens(Long chatId, TokenDTO tokenDTO);

    String getAccessToken(Long chatId);
}
