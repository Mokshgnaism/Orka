package com.Orka.publisher;

import com.Orka.apiContract.generated.ScriptExecutionResult;
import com.Orka.events.KafkaTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class KafkaPublisher {
    private final KafkaTemplate<String,byte[]> kafkaTemplate;

    public KafkaPublisher(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public void publish_scriptExecutionFailed(UUID id){

    }
    public void publish_scriptExecution(ScriptExecutionResult event){
        kafkaTemplate.send(
                KafkaTopics.SCRIPT_COMPLETED,
                event.toByteArray()
        );
    }
}
