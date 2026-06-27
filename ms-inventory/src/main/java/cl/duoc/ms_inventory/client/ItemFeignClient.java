package cl.duoc.ms_inventory.client;

import cl.duoc.ms_inventory.dto.ItemDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@FeignClient(name = "ms-item", url = "http://ms-item:8092/api/v1/item")
public interface ItemFeignClient {

    @GetMapping("/getItemId/{id}")
    ItemDto getItemById(@PathVariable("id") Long id);
}