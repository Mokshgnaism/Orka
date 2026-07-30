package com.Orka.grpc;

import com.Orka.apiContract.generated.CreateWorkflowDefinitionRequest;
import com.Orka.apiContract.generated.CreateWorkflowDefinitionResponse;
import com.Orka.apiContract.generated.GetAllWorkflowDefinitionsRequest;
import com.Orka.apiContract.generated.GetAllWorkflowDefinitionsResponse;
import com.Orka.repository.WorkflowDefinitionRepository;
import com.Orka.service.DefinitionManagerService;
import com.Orka.testutil.TestFixtures;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DefinitionMangerControllerTest {

    @Mock
    private WorkflowDefinitionRepository workflowDefinitionRepository;

    @Mock
    private DefinitionManagerService definitionManagerService;

    @InjectMocks
    private DefinitionMangerController controller;

    @Test
    void createWorkflowDefinition_delegatesToService() {
        CreateWorkflowDefinitionRequest request = TestFixtures.minimalWorkflowRequest();
        StreamObserver<CreateWorkflowDefinitionResponse> observer = new NoOpObserver<>();

        controller.createWorkflowDefinition(request, observer);

        verify(definitionManagerService).createWorkflowDefinition(request, observer);
    }

    @Test
    void getAllWorkflowDefinitions_delegatesToServiceWithUsername() {
        GetAllWorkflowDefinitionsRequest request = GetAllWorkflowDefinitionsRequest.newBuilder()
                .setUsername("viewer")
                .build();
        StreamObserver<GetAllWorkflowDefinitionsResponse> observer = new NoOpObserver<>();

        controller.getAllWorkflowDefinitions(request, observer);

        verify(definitionManagerService).getAllWorkflowDefinitions(observer, "viewer");
    }

    private static class NoOpObserver<T> implements StreamObserver<T> {
        @Override
        public void onNext(T value) {
        }

        @Override
        public void onError(Throwable t) {
        }

        @Override
        public void onCompleted() {
        }
    }
}
