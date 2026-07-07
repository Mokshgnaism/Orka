package com.Orka.repository;
import com.Orka.entities.definition.TaskDefinition;
import  com.Orka.entities.runtime.TaskRun;
import com.Orka.entities.runtime.WorkflowRun;
import com.Orka.interfaces.Repository;
import java.util.UUID;

public interface TaskRunRepository extends Repository {

    TaskRun findById(UUID id);
//    TaskRun findByName(String name);
    TaskRun findByWorkflowRunAndTaskDefinition(UUID workflowRunId,UUID taskDefinitionId );
}
