package org.nikhil.bajajsolution;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BajajsolutionApplication implements CommandLineRunner {

    private final WebhookService webhookService;

    public BajajsolutionApplication(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    public static void main(String[] args) {
        SpringApplication.run(BajajsolutionApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("Starting Bajaj Finance test automation...");
        webhookService.startFlow();  // This triggers your service
        System.out.println("Flow completed.");
    }
}
