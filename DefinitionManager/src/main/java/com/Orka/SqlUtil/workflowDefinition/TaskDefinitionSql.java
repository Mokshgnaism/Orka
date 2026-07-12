package com.Orka.SqlUtil.workflowDefinition;

import com.Orka.entities.definition.WorkflowDefinition;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
public class TaskDefinitionSql {
    public static List<PreparedStatement> prepareSqlForInsertingTaskDefinition(Connection connection, WorkflowDefinition workflowDefinition){
        PreparedStatement psTaskDefinitionInsert = null;
        String sql = """
        INSERT INTO task_definition (id,workflow_definition_id,name,description)
        VALUES (?,?,?,?)
        """;
        try{
            psTaskDefinitionInsert = connection.prepareStatement(sql);
            UUID workflowDefinitionId = workflowDefinition.getId();
            for(var taskDef : workflowDefinition.getTasks()){
                psTaskDefinitionInsert.setObject(1, taskDef.getId());
                assert taskDef.getWorkflowDefinitionId().equals(workflowDefinitionId);
                psTaskDefinitionInsert.setObject(2, taskDef.getWorkflowDefinitionId());
                psTaskDefinitionInsert.setString(3, taskDef.getName());
                psTaskDefinitionInsert.setString(4, taskDef.getDescription());
                psTaskDefinitionInsert.addBatch();
            }
        }catch (SQLException sqlException){
            log.error(sqlException.getMessage(),sqlException);
            log.error("[preparing for Task Definitions failed ]");
            sqlException.printStackTrace();
        }
        List<PreparedStatement> psStateDefinitionInserts = StateDefinitionSql.prepareSqlForInsertingStateDefinition(connection, workflowDefinition);

        List<PreparedStatement> psTaskDefinitionInserts = new ArrayList<>();
        psTaskDefinitionInserts.add(psTaskDefinitionInsert);
        psTaskDefinitionInserts.addAll(psStateDefinitionInserts);
        return psTaskDefinitionInserts;
    }
}
