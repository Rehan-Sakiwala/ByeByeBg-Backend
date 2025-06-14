package in.bg.ByeByeBG;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ByeByeBgApplication {

	public static void main(String[] args) {
		SpringApplication.run(ByeByeBgApplication.class, args);
	}

}