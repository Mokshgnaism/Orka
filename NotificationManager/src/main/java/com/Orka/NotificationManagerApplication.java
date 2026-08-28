package com.Orka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication(scanBasePackages = "com.Orka")
@EnableKafka
@EntityScan(basePackages = "com.Orka")
@EnableJpaRepositories(basePackages = "com.Orka.repository")
public class NotificationManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationManagerApplication.class, args);
    }
}
