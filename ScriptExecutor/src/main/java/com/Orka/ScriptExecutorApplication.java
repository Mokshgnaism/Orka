package com.Orka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
@EntityScan(basePackages = "com.Orka")
public class ScriptExecutorApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScriptExecutorApplication.class, args);
    }
}