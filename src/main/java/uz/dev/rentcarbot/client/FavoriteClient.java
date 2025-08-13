package uz.dev.rentcarbot.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.dev.rentcarbot.payload.FavoriteDTO;
import uz.dev.rentcarbot.payload.PageableDTO;
import uz.dev.rentcarbot.payload.TgFavoriteDTO;

/**
 * Created by: asrorbek
 * DateTime: 8/9/25 21:10
 **/

@FeignClient(name = "favorite-client", url = "${services.rent-car-service.url}/api/favorite")
public interface FavoriteClient {

    @PostMapping
    FavoriteDTO createFavorite(@RequestBody FavoriteDTO favoriteDTO);

    @GetMapping("/checked")
    TgFavoriteDTO getCheckFavorite(@RequestParam Long userId, @RequestParam Long carId);

    @DeleteMapping("/{id}")
    ResponseEntity<String> deleteFavorite(@PathVariable Long id);

    @GetMapping("/my")
    PageableDTO<FavoriteDTO> getMyFavorites(@RequestParam int page, @RequestParam int size);

}
