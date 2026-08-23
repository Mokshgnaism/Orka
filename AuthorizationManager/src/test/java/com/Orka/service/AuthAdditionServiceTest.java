package com.Orka.service;

import com.Orka.Store.Store;
import com.Orka.entities.authorization.WorkflowDefinitionAuthorization;
import com.Orka.entities.definition.WorkflowDefinition;
import com.Orka.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthAdditionServiceTest {

    @Mock
    private WorkflowDefinitionRepository workflowDefinitionRepository;
    @Mock
    private WorkflowRunRepository workflowRunRepository;
    @Mock
    private TaskDefinitionAuthorizationRepository taskDefinitionAuthorizationRepository;
    @Mock
    private WorkflowDefinitionAuthorizationRepository workflowDefinitionAuthorizationRepository;
    @Mock
    private TaskRunAuthorizationRepository taskRunAuthorizationRepository;
    @Mock
    private WorkflowRunAuthorizationRepository workflowRunAuthorizationRepository;

    @InjectMocks
    private AuthAdditionService authAdditionService;

    @BeforeEach
    void setUp() {
        Store.workflowDefinitionAuthorizations.clear();
        Store.taskDefinitionAuthorizations.clear();
        Store.workflowRunAuthorizations.clear();
        Store.taskRunAuthorizations.clear();
    }

    @Test
    void testAddDefinitionAuthToMemory() {
        UUID workflowId = UUID.randomUUID();
        WorkflowDefinition workflowDefinition = new WorkflowDefinition();
        workflowDefinition.setId(workflowId);
        
        WorkflowDefinitionAuthorization auth = WorkflowDefinitionAuthorization.builder()
                .username("user1")
                .workflowDefinitionId(workflowId)
                .build();
        
        workflowDefinition.setAuthorizationList(Collections.singletonList(auth));
        workflowDefinition.setTasks(Collections.emptyList());

        when(workflowDefinitionRepository.findById(workflowId)).thenReturn(Optional.of(workflowDefinition));

        authAdditionService.addDefinitionAuthToMemory(workflowId);

        assertTrue(Store.workflowDefinitionAuthorizations.containsKey("user1"));
        assertTrue(Store.workflowDefinitionAuthorizations.get("user1").contains(auth));
    }

    @Test
    void testAddDefinitionAuthToMemoryNotFound() {
        UUID workflowId = UUID.randomUUID();
        when(workflowDefinitionRepository.findById(workflowId)).thenReturn(Optional.empty());

        authAdditionService.addDefinitionAuthToMemory(workflowId);

        assertTrue(Store.workflowDefinitionAuthorizations.isEmpty());
    }
}
