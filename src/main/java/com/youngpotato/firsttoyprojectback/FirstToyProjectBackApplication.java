package com.youngpotato.firsttoyprojectback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class FirstToyProjectBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(FirstToyProjectBackApplication.class, args);
	}

}
