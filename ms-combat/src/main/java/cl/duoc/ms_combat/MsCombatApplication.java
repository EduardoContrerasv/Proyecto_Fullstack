package cl.duoc.ms_combat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableFeignClients
public class MsCombatApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsCombatApplication.class, args);
	}

}
