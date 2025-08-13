package uz.dev.rentcarbot.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/open/{id}")
    OfficeDTO getOfficeById(@PathVariable Long id);

    @GetMapping("/admin")
    PageableDTO<OfficeDTO> getAllOfficesAdmin(@RequestParam int page, @RequestParam int size);

    @PostMapping("/admin")
    OfficeDTO createOffice(@RequestBody OfficeDTO officeDTO);

    @PutMapping("/admin/{id}")
    OfficeDTO updateOffice(@PathVariable Long id, @RequestBody OfficeDTO officeDTO);

    @GetMapping("/admin/{id}")
    OfficeDTO getOfficeByIdAdmin(@PathVariable Long id);

    @DeleteMapping("/admin/{id}")
    void deleteOffice(@PathVariable Long id);
}