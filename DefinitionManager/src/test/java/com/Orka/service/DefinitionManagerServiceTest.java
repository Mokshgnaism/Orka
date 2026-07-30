package com.Orka.service;

import com.Orka.apiContract.generated.CreateWorkflowDefinitionRequest;
import com.Orka.apiContract.generated.CreateWorkflowDefinitionResponse;
import com.Orka.apiContract.generated.GetAllWorkflowDefinitionsResponse;
import com.Orka.entities.definition.WorkflowDefinition;
import com.Orka.publisher.KafkaPublisher;
import com.Orka.repository.WorkflowDefinitionRepository;
import com.Orka.testutil.TestFixtures;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefinitionManagerServiceTest {

    @Mock
    private WorkflowDefinitionRepository workflowDefinitionRepository;

    @Mock
    private KafkaPublisher kafkaPublisher;

    @InjectMocks
    private DefinitionManagerService definitionManagerService;

    private CreateWorkflowDefinitionRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = TestFixtures.minimalWorkflowRequest();
    }

    @Test
    void createWorkflowDefinition_success() {
        CapturingObserver<CreateWorkflowDefinitionResponse> observer = new CapturingObserver<>();

        definitionManagerService.createWorkflowDefinition(validRequest, observer);

        ArgumentCaptor<WorkflowDefinition> savedCaptor = ArgumentCaptor.forClass(WorkflowDefinition.class);
        verify(workflowDefinitionRepository).save(savedCaptor.capture());
        verify(kafkaPublisher).publish_workflowDefinitionStarted(savedCaptor.getValue().getId());

        assertFalse(observer.completedWithError);
        assertNotNull(observer.response);
        assertEquals(savedCaptor.getValue().getId().toString(), observer.response.getWorkflowDefinitionId());
        assertTrue(observer.completed);
    }

    @Test
    void createWorkflowDefinition_onError_sendsInternalStatus() {
        when(workflowDefinitionRepository.save(any())).thenThrow(new RuntimeException("db down"));
        CapturingObserver<CreateWorkflowDefinitionResponse> observer = new CapturingObserver<>();

        definitionManagerService.createWorkflowDefinition(validRequest, observer);

        assertTrue(observer.completedWithError);
        assertInstanceOf(StatusRuntimeException.class, observer.error);
        assertEquals(Status.Code.INTERNAL, ((StatusRuntimeException) observer.error).getStatus().getCode());
        assertEquals("db down", observer.error.getMessage());
        verify(kafkaPublisher, never()).publish_workflowDefinitionStarted(any());
    }

    @Test
    void getAllWorkflowDefinitions_returnsAuthorizedWorkflows() {
        WorkflowDefinition workflow = TestFixtures.persistedWorkflowDefinition();
        when(workflowDefinitionRepository.findAuthorizedWorkflows("viewer")).thenReturn(List.of(workflow));
        CapturingObserver<GetAllWorkflowDefinitionsResponse> observer = new CapturingObserver<>();

        definitionManagerService.getAllWorkflowDefinitions(observer, "viewer");

        assertFalse(observer.completedWithError);
        assertTrue(observer.completed);
        assertEquals(1, observer.response.getWorkflowDefinitionsCount());
        assertEquals(workflow.getId().toString(), observer.response.getWorkflowDefinitions(0).getId());
        assertEquals(workflow.getName(), observer.response.getWorkflowDefinitions(0).getName());
        assertEquals("viewer", observer.response.getWorkflowDefinitions(0).getAuthorizations(0).getUsername());
    }

    @Test
    void getAllWorkflowDefinitions_emptyList() {
        when(workflowDefinitionRepository.findAuthorizedWorkflows("nobody")).thenReturn(List.of());
        CapturingObserver<GetAllWorkflowDefinitionsResponse> observer = new CapturingObserver<>();

        definitionManagerService.getAllWorkflowDefinitions(observer, "nobody");

        assertTrue(observer.completed);
        assertEquals(0, observer.response.getWorkflowDefinitionsCount());
    }

    private static class CapturingObserver<T> implements StreamObserver<T> {
        private T response;
        private Throwable error;
        private boolean completed;
        private boolean completedWithError;

        @Override
        public void onNext(T value) {
            this.response = value;
        }

        @Override
        public void onError(Throwable t) {
            this.error = t;
            this.completedWithError = true;
        }

        @Override
        public void onCompleted() {
            this.completed = true;
        }
    }
}
