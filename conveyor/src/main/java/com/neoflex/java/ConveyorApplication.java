package com.neoflex.java;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@SuppressWarnings("unused")
public class ConveyorApplication {
    public static void main(String[] args) {
        ApplicationContext context = new SpringApplicationBuilder(ConveyorApplication.class).headless(false).run(args);
    }
}
