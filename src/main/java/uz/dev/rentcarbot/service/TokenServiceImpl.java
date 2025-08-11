package uz.dev.rentcarbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import uz.dev.rentcarbot.client.AuthClient;
import uz.dev.rentcarbot.payload.TokenDTO;
import uz.dev.rentcarbot.service.template.TokenService;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final AuthClient authFeignClient;

    @Override
    public void saveTokens(Long chatId, TokenDTO tokenDTO) {
        redisTemplate.opsForValue().set(getAccessKey(chatId), tokenDTO.getAccessToken(), Duration.ofHours(3));
        redisTemplate.opsForValue().set(getRefreshKey(chatId), tokenDTO.getRefreshToken(), Duration.ofHours(12));
    }

    @Override
    public String getAccessToken(Long chatId) {

        String accessToken = (String) redisTemplate.opsForValue().get(getAccessKey(chatId));

        if (accessToken == null) {

            String refreshToken = (String) redisTemplate.opsForValue().get(getRefreshKey(chatId));

            if (refreshToken == null) {
                return null;
            }
            TokenDTO newTokens = authFeignClient.verifyToken(refreshToken);
            saveTokens(chatId, newTokens);
            accessToken = newTokens.getAccessToken();
        }

        return accessToken;
    }

    private String getAccessKey(Long chatId) {
        return "access:" + chatId;
    }

    private String getRefreshKey(Long chatId) {
        return "refresh:" + chatId;
    }
}

