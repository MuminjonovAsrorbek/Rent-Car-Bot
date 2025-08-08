package uz.dev.rentcarbot.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uz.dev.rentcarbot.payload.TgUserDTO;

/**
 * Created by: asrorbek
 * DateTime: 8/8/25 16:16
 **/

@FeignClient(name = "user-client", url = "${services.rent-car-service.url}/api/users")
public interface UserClient {

    @GetMapping("/open/telegram/get-by-phoneNumber/{phoneNumber}")
    TgUserDTO isRegistered(@PathVariable String phoneNumber);

}
