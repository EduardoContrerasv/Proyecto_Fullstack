package cl.duoc.ms_characters;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableDiscoveryClient
@SpringBootApplication
@EntityScan(basePackages = "cl.duoc.ms_characters.model")
@EnableJpaRepositories(basePackages = "cl.duoc.ms_characters.repository")
@EnableFeignClients
public class MsCharactersApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsCharactersApplication.class, args);
	}

}
