package cl.duoc.ms_characters.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-inventory", url = "http://ms-inventory:8093/api/v1/inventory")
public interface InventoryClient {

    @GetMapping("/user/{userId}/item/{itemId}/check")
    boolean checkHasItem(@PathVariable("userId") Long userId, @PathVariable("itemId") Long itemId);
}