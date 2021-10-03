package no.ntnu.idatt210620217.giddapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages={"no.ntnu.idatt210620217.giddapi"})
public class GiddApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(GiddApiApplication.class, args);	
	}

}
