package com.neoflex.java;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableFeignClients
@SuppressWarnings("unused")
public class ApplicationApp {
    public static void main(String[] args) {
        ApplicationContext context = new SpringApplicationBuilder(ApplicationApp.class).headless(false).run(args);
    }
}
