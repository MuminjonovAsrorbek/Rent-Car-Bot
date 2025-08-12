package uz.dev.rentcarbot.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import uz.dev.rentcarbot.payload.BookingCreateDTO;
import uz.dev.rentcarbot.payload.BookingDTO;

/**
 * Created by: asrorbek
 * DateTime: 8/10/25 22:27
 **/

@FeignClient(name = "booking-client", url = "${services.rent-car-service.url}/api/bookings")
public interface BookingClient {

    @PostMapping
    BookingDTO createBooking(@RequestBody BookingCreateDTO bookingCreateDTO);
}
