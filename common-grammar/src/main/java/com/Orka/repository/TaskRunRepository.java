package com.Orka.repository;

import com.Orka.entities.runtime.TaskRun;
import com.Orka.entities.runtime.WorkflowRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface TaskRunRepository extends JpaRepository<TaskRun, UUID> {
    TaskRun findByWorkflowRunAndTaskDefinition_Id(WorkflowRun workflowRun, UUID taskDefinitionId);
}
