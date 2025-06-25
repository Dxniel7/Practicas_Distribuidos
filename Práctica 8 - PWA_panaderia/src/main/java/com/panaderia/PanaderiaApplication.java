package com.panaderia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // Habilitar la programación de tareas
public class PanaderiaApplication {

    public static void main(String[] args) {
        SpringApplication.run(PanaderiaApplication.class, args);
    }
}
