package com.starknakedpoultry.starkorders;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.starknakedpoultry")
public class StarkordersApplication {

	public static void main(String[] args) {
		SpringApplication.run(StarkordersApplication.class, args);
	}

}
