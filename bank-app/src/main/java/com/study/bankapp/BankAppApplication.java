package com.study.bankapp;

import org.hibernate.validator.internal.engine.ConfigurationImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Arrays;

@EnableJpaAuditing
@SpringBootApplication
public class BankAppApplication {

	public static void main(String[] args) {

		 SpringApplication.run(BankAppApplication.class, args);


	}

}
