package com.Orka.repository;

import com.Orka.entities.definition.WorkflowDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface WorkflowDefinitionRepository extends JpaRepository<WorkflowDefinition, UUID> {
    @Query("""
    SELECT wd
    FROM WorkflowDefinition wd
    JOIN wd.authorizationList auth
    WHERE auth.username = :username
    """)
    List<WorkflowDefinition> findAuthorizedWorkflows(@Param("username") String username);
}
