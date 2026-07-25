package com.Orka.repository;

import com.Orka.entities.authorization.TaskDefinitionAuthorization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaskDefinitionAuthorizationRepository extends JpaRepository<TaskDefinitionAuthorization, UUID> {
}
