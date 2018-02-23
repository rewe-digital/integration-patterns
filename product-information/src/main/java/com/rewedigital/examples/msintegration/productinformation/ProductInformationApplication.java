package com.rewedigital.examples.msintegration.productinformation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProductInformationApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductInformationApplication.class, args);
	}
}
