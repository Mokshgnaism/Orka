package com.Orka.publisher;

import com.Orka.apiContract.generated.StateRunStartedEvent;
import com.Orka.entities.runtime.StateRun;
import com.Orka.events.KafkaTopics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StateRunEventPublisher {
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public StateRunEventPublisher(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public void publishStateRunStartedEvent(StateRun stateRun) {
        try{
            StateRunStartedEvent stateRunStartedEvent = StateRunStartedEvent.newBuilder().setId(stateRun.getId().toString()).build();
            kafkaTemplate.send(
                    KafkaTopics.STATE_ACTIVATED,
                    stateRun.getId().toString(),
                    stateRunStartedEvent.toByteArray()
            ).get();
            log.info("state activated event published for {} : {} ",stateRun.getId(),stateRun.getStateDefinition());
        }catch (Exception e){
            log.error(e.getMessage());
            log.error("[kafka unable to publish message for [state activated event] {}:{}",stateRun.getId(),stateRun.getStateDefinition());
            e.printStackTrace();
        }
    }
}
