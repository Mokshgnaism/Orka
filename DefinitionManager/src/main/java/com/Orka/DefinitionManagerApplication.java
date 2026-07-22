package com.Orka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.Orka")
public class DefinitionManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(DefinitionManagerApplication.class, args);
    }
}