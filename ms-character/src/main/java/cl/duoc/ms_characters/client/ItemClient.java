package cl.duoc.ms_characters.client;

import cl.duoc.ms_characters.dto.ItemFeignDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-item", url = "http://ms-item:8092/api/v1/item")
public interface ItemClient {

    @GetMapping("/getItemId/{id}")
    ItemFeignDto getItemById(@PathVariable("id") Long id);
}