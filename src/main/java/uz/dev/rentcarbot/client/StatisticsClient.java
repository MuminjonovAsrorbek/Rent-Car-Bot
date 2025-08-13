package uz.dev.rentcarbot.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import uz.dev.rentcarbot.payload.UserStatisticDTO;

/**
 * Created by: asrorbek
 * DateTime: 8/13/25 20:36
 **/

@FeignClient(name = "statistics-client", url = "${services.rent-car-service.url}/api/statistics")
public interface StatisticsClient {

    @GetMapping("/users")
    UserStatisticDTO getUserStatistics();

}
