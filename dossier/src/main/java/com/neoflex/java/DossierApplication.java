package com.neoflex.java;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@SuppressWarnings("unused")
public class DossierApplication {
    public static void main(String[] args) {
        ApplicationContext context = new SpringApplicationBuilder(DossierApplication.class).headless(false).run(args);
    }
}
