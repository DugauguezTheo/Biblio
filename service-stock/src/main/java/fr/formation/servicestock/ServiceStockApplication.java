package fr.formation.servicestock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ServiceStockApplication {
	public static void main(String[] args) {
		SpringApplication.run(ServiceStockApplication.class, args);
	}
}
