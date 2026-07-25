package com.Orka.repository;

import com.Orka.entities.authorization.TaskRunAuthorization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaskRunAuthorizationRepository extends JpaRepository<TaskRunAuthorization, UUID> {
}
