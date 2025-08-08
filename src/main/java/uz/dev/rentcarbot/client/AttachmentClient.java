package uz.dev.rentcarbot.client;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * Created by: asrorbek
 * DateTime: 8/8/25 20:44
 **/

@FeignClient(name = "attachment-client", url = "${services.rent-car-service.url}/api/attachments")
public interface AttachmentClient {


}
