package uz.dev.rentcarbot.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by: asrorbek
 * DateTime: 8/12/25 15:21
 **/

@FeignClient(name = "promo-code-client", url = "${services.rent-car-service.url}/api/promo-code")
public interface PromoCodeClient {

    @GetMapping("/open/validate")
    boolean codeValidate(@RequestParam String code);

}
