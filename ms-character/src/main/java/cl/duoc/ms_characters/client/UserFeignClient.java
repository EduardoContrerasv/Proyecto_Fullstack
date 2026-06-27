package cl.duoc.ms_characters.client;

import cl.duoc.ms_characters.dto.UserFeignDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-user", url = "http://ms-user:8090/api/v1/user")
public interface UserFeignClient {

    @GetMapping("/getUserId/{id}")
    UserFeignDto getUserById(@PathVariable("id") Long id);
}
