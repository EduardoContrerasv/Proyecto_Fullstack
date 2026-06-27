package cl.duoc.ms_shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsShopApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsShopApplication.class, args);
	}

}
