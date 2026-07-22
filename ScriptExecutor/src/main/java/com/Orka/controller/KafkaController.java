package com.Orka.controller;

import com.Orka.apiContract.generated.StateRunStartedEvent;
import com.Orka.events.KafkaTopics;
import com.Orka.service.ScriptRunner;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.shaded.com.ongres.scram.client.ScramClient;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import org.springframework.context.ApplicationContext;

@Slf4j
@Service
public class KafkaController {
    private final ScriptRunner scriptRunner;
    private final ApplicationContext applicationContext;


    public KafkaController(
            ScriptRunner scriptRunner,
            ApplicationContext applicationContext) {
        this.scriptRunner = scriptRunner;
        this.applicationContext = applicationContext;
    }
    @PostConstruct
    public void init() {
        System.out.println("Injected ScriptRunner : " + scriptRunner);
        System.out.println("Bean from context     : " + applicationContext.getBean(ScriptRunner.class));
        System.out.println("Same object           : " +
                (scriptRunner == applicationContext.getBean(ScriptRunner.class)));
    }

    @KafkaListener(
            topics = KafkaTopics.STATE_ACTIVATED,
            groupId = KafkaTopics.STATE_ACTIVATED + "-ScriptExecutor -group"
    )
    public void listenForStartingScripts(byte[] event){
        try{
            StateRunStartedEvent stateRunStartedEvent = StateRunStartedEvent.parseFrom(event);
            log.info("State Run Started Event Received: {}", stateRunStartedEvent);
            scriptRunner.detectAndStartScript(stateRunStartedEvent);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
            log.info("StateRunStartedEvent: " + e.getMessage());
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error("[HUMAN] listen failed ");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
