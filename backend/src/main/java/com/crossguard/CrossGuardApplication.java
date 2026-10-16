package com.crossguard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CrossGuardApplication {
    public static void main(String[] args) { SpringApplication.run(CrossGuardApplication.class, args); }
}
