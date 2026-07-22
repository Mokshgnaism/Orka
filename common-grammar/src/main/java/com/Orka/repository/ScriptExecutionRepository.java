package com.Orka.repository;

import com.Orka.entities.runtime.ScriptExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface ScriptExecutionRepository extends JpaRepository<ScriptExecution, UUID> {
    Optional<ScriptExecution> findByStateRun_Id(UUID id);
}
