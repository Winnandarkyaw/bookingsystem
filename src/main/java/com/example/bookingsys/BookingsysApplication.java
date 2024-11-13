package com.example.bookingsys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"com.example.bookingsys.model.*"})
@EnableJpaRepositories(basePackages={"com.example.bookingsys.repository.*"})
@ComponentScan(basePackages = {"com.example.bookingsys.service.*", "com.example.bookingsys.util.*"})

public class BookingsysApplication {
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(BookingsysApplication.class, args);

	}
}
