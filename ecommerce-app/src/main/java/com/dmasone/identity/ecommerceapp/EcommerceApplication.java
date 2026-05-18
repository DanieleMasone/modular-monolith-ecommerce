package com.dmasone.identity.ecommerceapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Single executable entry point for the modular monolith. The application
 * module composes catalog, orders, payment, and shared infrastructure without
 * moving business logic into the bootstrap layer.
 */
@EnableCaching
@EntityScan(basePackages = "com.dmasone.identity")
@EnableJpaRepositories(basePackages = "com.dmasone.identity")
@SpringBootApplication(scanBasePackages = "com.dmasone.identity")
public class EcommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommerceApplication.class, args);
    }
}
