package com.saf.restaurant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SafRestaurantApplication {

    public static void main(String[] args) {
        SpringApplication.run(SafRestaurantApplication.class, args);
    }
}
