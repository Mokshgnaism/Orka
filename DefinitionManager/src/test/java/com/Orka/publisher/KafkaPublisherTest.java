package com.Orka.publisher;

import com.Orka.events.KafkaTopics;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaPublisherTest {

    @Mock
    private KafkaTemplate<String, byte[]> kafkaTemplate;

    @InjectMocks
    private KafkaPublisher kafkaPublisher;

    @Test
    void publish_workflowDefinitionStarted_sendsEventToTopic() {
        UUID workflowId = UUID.randomUUID();

        kafkaPublisher.publish_workflowDefinitionStarted(workflowId);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<byte[]> payloadCaptor = ArgumentCaptor.forClass(byte[].class);

        verify(kafkaTemplate).send(topicCaptor.capture(), keyCaptor.capture(), payloadCaptor.capture());

        assertEquals(KafkaTopics.WORKFLOW_DEFINITION_CREATED, topicCaptor.getValue());
        assertEquals(workflowId.toString(), keyCaptor.getValue());
        assertNotNull(payloadCaptor.getValue());
        assertTrue(payloadCaptor.getValue().length > 0);
    }
}
