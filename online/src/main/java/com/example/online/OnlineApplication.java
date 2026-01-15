package com.example.online;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OnlineApplication{
	public static void main(String[] args) {
        SpringApplication.run(OnlineApplication.class, args);
	}
}
