package uz.dev.rentcarbot.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import uz.dev.rentcarbot.payload.CategoryDTO;
import uz.dev.rentcarbot.payload.PageableDTO;

@FeignClient(name = "category-client", url = "${services.rent-car-service.url}/api/category")
public interface CategoryClient {

    @GetMapping("/admin")
    PageableDTO<CategoryDTO> getAllCategories(@RequestParam int page, @RequestParam int size);

    @GetMapping("/admin/{id}")
    CategoryDTO getCategoryById(@PathVariable Long id);

    @PostMapping("/admin")
    CategoryDTO createCategory(@RequestBody CategoryDTO categoryDTO);

    @PutMapping("/admin/{id}")
    CategoryDTO updateCategory(@PathVariable Long id, @RequestBody CategoryDTO categoryDTO);

    @DeleteMapping("/admin/{id}")
    void deleteCategory(@PathVariable Long id);
}
