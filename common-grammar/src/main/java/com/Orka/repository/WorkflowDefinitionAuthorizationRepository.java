package com.Orka.repository;

import com.Orka.entities.authorization.WorkflowDefinitionAuthorization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WorkflowDefinitionAuthorizationRepository extends JpaRepository<WorkflowDefinitionAuthorization, UUID> {
}
