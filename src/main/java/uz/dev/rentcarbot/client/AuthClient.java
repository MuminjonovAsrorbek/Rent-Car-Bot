package uz.dev.rentcarbot.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import uz.dev.rentcarbot.payload.TokenDTO;

/**
 * Created by: asrorbek
 * DateTime: 8/8/25 16:07
 **/

@FeignClient(name = "auth-client", url = "${services.rent-car-service.url}/api/auth")
public interface AuthClient {

    @GetMapping("/telegram/get-token/{phoneNumber}")
    TokenDTO getTokenByPhoneNumber(@PathVariable String phoneNumber);

    @GetMapping("/verify")
    TokenDTO verifyToken(@RequestParam String refreshToken);
}
