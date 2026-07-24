package br.com.agendamento.psicologia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PsicologiaApplication {

    public static void main(String[] args) {
        SpringApplication.run(PsicologiaApplication.class, args);
    }
}