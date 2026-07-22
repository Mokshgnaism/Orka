package com.Orka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.kafka.annotation.EnableKafka;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
@SpringBootApplication
@EnableKafka
@EntityScan(basePackages = "com.Orka")
public class Automata {
    public static void main(String[] args) {
        SpringApplication.run(Automata.class, args);
    }
}