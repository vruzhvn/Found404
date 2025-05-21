package ru.javabegin.backend.found404;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "ru.javabegin.backend.found404.model") // Укажите пакет, где находятся ваши сущности
@EnableJpaRepositories(basePackages = "ru.javabegin.backend.found404.repository") // Укажите пакет, где находятся репозитории
public class Found404Application {

    public static void main(String[] args) {
        SpringApplication.run(Found404Application.class, args);
    }
}
