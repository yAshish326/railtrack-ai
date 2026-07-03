package com.railtrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RailtrackBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(RailtrackBackendApplication.class, args);
	}
}
