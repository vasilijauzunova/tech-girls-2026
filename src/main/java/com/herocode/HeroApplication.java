package com.herocode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Starts the web server. Run this file (or use "mvn spring-boot:run") and open port 8080. */
@SpringBootApplication
public class HeroApplication {

    public static void main(String[] args) {
        SpringApplication.run(HeroApplication.class, args);
    }
}
