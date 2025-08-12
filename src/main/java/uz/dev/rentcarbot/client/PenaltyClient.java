package uz.dev.rentcarbot.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uz.dev.rentcarbot.payload.PageableDTO;
import uz.dev.rentcarbot.payload.PenaltyDTO;

/**
 * Created by: asrorbek
 * DateTime: 8/12/25 16:47
 **/

@FeignClient(name = "penalty-client", url = "${services.rent-car-service.url}/api/penalties")
public interface PenaltyClient {

    @GetMapping("/me/overdue-returns")
    PageableDTO<PenaltyDTO> getMyOverdueReturns(@RequestParam int page, @RequestParam int size);

    @GetMapping("/me/all-penalties")
    PageableDTO<PenaltyDTO> getMyPenalties(@RequestParam int page, @RequestParam int size);

}
