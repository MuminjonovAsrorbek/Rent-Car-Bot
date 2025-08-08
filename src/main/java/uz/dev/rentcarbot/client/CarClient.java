package uz.dev.rentcarbot.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uz.dev.rentcarbot.payload.CarDTO;
import uz.dev.rentcarbot.payload.PageableDTO;

/**
 * Created by: asrorbek
 * DateTime: 8/8/25 18:34
 **/

@FeignClient(name = "car-client", url = "${services.rent-car-service.url}/api/cars")
public interface CarClient {

    @GetMapping("/open/available")
    PageableDTO<CarDTO> getAvailableCars(@RequestParam int page, @RequestParam int size);

}
