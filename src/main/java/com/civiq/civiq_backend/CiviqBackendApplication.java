package com.civiq.civiq_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CiviqBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CiviqBackendApplication.class, args);
	}

}
