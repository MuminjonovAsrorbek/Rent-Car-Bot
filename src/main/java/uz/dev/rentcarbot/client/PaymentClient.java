package uz.dev.rentcarbot.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import uz.dev.rentcarbot.payload.PaymentDTO;

/**
 * Created by: asrorbek
 * DateTime: 8/13/25 22:23
 **/

@FeignClient(name = "payment-client", url = "${services.rent-car-service.url}/api/payments")
public interface PaymentClient {

    @PutMapping("/{bookingId}/confirm")
    PaymentDTO confirmPayment(@PathVariable Long bookingId);

    @PutMapping("/{bookingId}/cancel")
    PaymentDTO cancelPayment(@PathVariable Long bookingId);

}
