package com.example.public_transportation_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class PublicTransportationProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(PublicTransportationProjectApplication.class, args);
	}

}
