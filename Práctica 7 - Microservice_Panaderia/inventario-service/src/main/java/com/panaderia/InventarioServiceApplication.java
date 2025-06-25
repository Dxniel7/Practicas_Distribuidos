package com.panaderia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InventarioServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventarioServiceApplication.class, args);
	}

}
 