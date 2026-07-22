package com.Orka;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;

import java.util.Arrays;

@SpringBootApplication
@EntityScan(basePackages = "com.Orka")
@EnableKafka
public class RunManagerApplication {
    public static void main(String[] args) {
       SpringApplication.run(RunManagerApplication.class, args);
    }
    @Bean
    ApplicationRunner beans(ApplicationContext context) {
        return args -> {
            Arrays.stream(context.getBeanDefinitionNames())
                    .filter(n -> n.toLowerCase().contains("run"))
                    .sorted()
                    .forEach(System.out::println);
        };
    }
}