package uz.dev.rentcarbot.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("UserToken")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserToken {

    @Id
    private String chatId; // Redis key bo'ladi

    private String accessToken;

    private String refreshToken;
}
