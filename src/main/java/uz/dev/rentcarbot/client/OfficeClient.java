package uz.dev.rentcarbot.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uz.dev.rentcarbot.payload.OfficeDTO;
import uz.dev.rentcarbot.payload.PageableDTO;

/**
 * Created by: asrorbek
 * DateTime: 8/10/25 21:15
 **/

@FeignClient(name = "office-client", url = "${services.rent-car-service.url}/api/office")
public interface OfficeClient {

    @GetMapping("/open")
    PageableDTO<OfficeDTO> getAllOffices(@RequestParam int page, @RequestParam int size);

}
