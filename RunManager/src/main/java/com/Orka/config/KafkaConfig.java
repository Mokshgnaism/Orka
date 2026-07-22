package com.Orka.config;

import com.Orka.events.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic workflowCreatedTopic() {
        return new NewTopic(
                KafkaTopics.WORKFLOW_CREATED,
                1,          // number of partitions
                (short) 1   // replication factor
        );
    }
}