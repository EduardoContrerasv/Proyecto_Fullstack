package cl.duoc.ms_shop.client;

import cl.duoc.ms_shop.dto.CurrencyFeignDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-currency", url = "http://ms-currency:8095/api/v1/currency")
public interface CurrencyClient {

    @PostMapping("/deduct/{userId}")
    String deductCurrency(@PathVariable("userId") Long userId, @RequestBody CurrencyFeignDto dto);

    @PostMapping("/add/{userId}")
    String addCurrency(@PathVariable("userId") Long userId, @RequestBody CurrencyFeignDto dto);
}
