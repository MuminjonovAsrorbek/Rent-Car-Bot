package uz.dev.rentcarbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.dev.rentcarbot.model.UserToken;
import uz.dev.rentcarbot.repository.UserTokenRepository;
import uz.dev.rentcarbot.service.template.TokenService;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final UserTokenRepository repository;

    @Override
    public void saveTokens(String chatId, String accessToken, String refreshToken) {
        UserToken token = new UserToken(chatId, accessToken, refreshToken);
        repository.save(token);
    }

    @Override
    public UserToken getTokens(String chatId) {
        return repository.findById(chatId).orElse(null);
    }

    @Override
    public void deleteTokens(String chatId) {
        repository.deleteById(chatId);
    }
}
