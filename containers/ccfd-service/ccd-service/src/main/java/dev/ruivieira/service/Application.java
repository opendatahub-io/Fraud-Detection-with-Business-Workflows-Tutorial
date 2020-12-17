package dev.ruivieira.service;

import dev.ruivieira.service.prediction.SeldonPredictionService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        System.setProperty("org.kie.prometheus.server.ext.disabled", "false");
        System.setProperty("org.jbpm.task.prediction.service", SeldonPredictionService.IDENTIFIER);
        SpringApplication.run(Application.class, args);
    }

}