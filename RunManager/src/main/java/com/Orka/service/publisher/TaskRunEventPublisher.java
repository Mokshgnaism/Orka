package com.Orka.service.publisher;

import com.Orka.events.KafkaTopics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class TaskRunEventPublisher {
    private final KafkaTemplate<String,byte[]>kafkaTemplate;

    public TaskRunEventPublisher(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish_input_provided_event(UUID taskRunId, byte [] event){
        try {
            kafkaTemplate.send(
                    KafkaTopics.TASK_INPUT_PROVIDED,
                    taskRunId.toString(),
                    event
            );
        }catch (Exception e){
            log.error("[HUMAN] error publushing input provided event");
            e.printStackTrace();
        }
    }


}
