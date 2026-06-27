package cl.duoc.ms_quest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsQuestApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsQuestApplication.class, args);
	}

}
