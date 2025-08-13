package uz.dev.rentcarbot.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import uz.dev.rentcarbot.payload.BookingCreateDTO;
import uz.dev.rentcarbot.payload.BookingDTO;
import uz.dev.rentcarbot.payload.PageableDTO;

/**
 * Created by: asrorbek
 * DateTime: 8/10/25 22:27
 **/

@FeignClient(name = "booking-client", url = "${services.rent-car-service.url}/api/bookings")
public interface BookingClient {

    @PostMapping
    BookingDTO createBooking(@RequestBody BookingCreateDTO bookingCreateDTO);

    @GetMapping("/my")
    PageableDTO<BookingDTO> getMyBookings(@RequestParam int page, @RequestParam int size);

    @PostMapping("/{id}/confirm")
    BookingDTO confirmBooking(@PathVariable Long id);

    @PostMapping("/{id}/complete")
    BookingDTO completeBooking(@PathVariable Long id);

    @PostMapping("/{id}/cancel")
    BookingDTO cancelBooking(@PathVariable Long id);
}
