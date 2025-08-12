package uz.dev.rentcarbot.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uz.dev.rentcarbot.payload.NotificationDTO;
import uz.dev.rentcarbot.payload.PageableDTO;

/**
 * Created by: asrorbek
 * DateTime: 8/12/25 19:23
 **/

@FeignClient(name = "notification-client", url = "${services.rent-car-service.url}/api/notification")
public interface NotificationClient {

    @GetMapping("/my/all-notifications")
    PageableDTO<NotificationDTO> getMyAllNotifications(@RequestParam int page, @RequestParam int size);

    @GetMapping("/my/unread-notifications")
    PageableDTO<NotificationDTO> getMyUnreadNotifications(@RequestParam int page, @RequestParam int size);

}
