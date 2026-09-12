package com.cards.flash;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class FlashApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlashApplication.class, args);
    }

    // Define the exact bean that CardServiceImpl is asking for
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}