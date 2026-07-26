package com.Orka.repository;

import com.Orka.entities.authorization.WorkflowRunAuthorization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WorkflowRunAuthorizationRepository extends JpaRepository<WorkflowRunAuthorization, UUID> {
}
