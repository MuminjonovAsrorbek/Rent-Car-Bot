package uz.dev.rentcarbot.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import uz.dev.rentcarbot.payload.PageableDTO;
import uz.dev.rentcarbot.payload.ReviewDTO;

/**
 * Created by: asrorbek
 * DateTime: 8/9/25 19:40
 **/

@FeignClient(name = "review-client", url = "${services.rent-car-service.url}/api/review")
public interface ReviewClient {

    @GetMapping("/open/car/{carId}")
    PageableDTO<ReviewDTO> getReviewsByCarId(@PathVariable Long carId,
                                             @RequestParam int page, @RequestParam int size);

}
