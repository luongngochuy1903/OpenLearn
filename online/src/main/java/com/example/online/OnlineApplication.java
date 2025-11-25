package com.example.online;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OnlineApplication implements CommandLineRunner {
    @PersistenceContext
    private EntityManager em;

	public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure()
                .directory("./")
                .filename(".env")
                .load();
        System.setProperty("app.jwt.secret", dotenv.get("SECRET_KEY"));

        SpringApplication.run(OnlineApplication.class, args);

	}
    @Override
    public void run(String... args) {
        System.out.println("Entities managed by Hibernate:");
        em.getMetamodel().getEntities().forEach(e -> System.out.println(e.getName()));
    }

}
