package uz.dev.rentcarbot.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.dev.rentcarbot.payload.CarDTO;
import uz.dev.rentcarbot.payload.PageableDTO;
import java.util.List;

/**
 * Created by: asrorbek
 * DateTime: 8/8/25 18:34
 **/

@FeignClient(name = "car-client", url = "${services.rent-car-service.url}/api/cars")
public interface CarClient {

    @GetMapping("/open/available")
    PageableDTO<CarDTO> getAvailableCars(@RequestParam int page, @RequestParam int size);

    @GetMapping("/open/{id}")
    CarDTO getCarById(@PathVariable Long id);

    @PostMapping
    CarDTO createCar(@RequestBody CarDTO carDto);

    @PutMapping("/{id}")
    CarDTO updateCar(@PathVariable Long id, @RequestBody CarDTO carDto);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteCar(@PathVariable Long id);

    @GetMapping
    List<CarDTO> getAllCars();

}
