package com.Orka.repository;
import  com.Orka.entities.runtime.TaskRun;
import javax.sql.DataSource;
import java.util.UUID;

public class jdbcTaskRunRepository implements TaskRunRepository {

    private DataSource dataSource;
    @Override
    public TaskRun findById(UUID id){
        return new TaskRun();
    }

    @Override
    public TaskRun findByWorkflowRunAndTaskDefinition(UUID workflowRunId, UUID taskDefinitionId) {
        return null;
    }

}
