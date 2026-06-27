package cl.duoc.ms_shop.client;


import cl.duoc.ms_shop.dto.InventoryFeignDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-inventory", url = "http://ms-inventory:8093/api/v1/inventory")
public interface InventoryClient {
    @PostMapping("/add")
    void addItem(@RequestBody InventoryFeignDto dto);
}
