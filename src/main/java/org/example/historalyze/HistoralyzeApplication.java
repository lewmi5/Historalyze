package org.example.historalyze;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class HistoralyzeApplication {
    public static void main(String[] args) {
        SpringApplication.run(HistoralyzeApplication.class, args);
    }
}
