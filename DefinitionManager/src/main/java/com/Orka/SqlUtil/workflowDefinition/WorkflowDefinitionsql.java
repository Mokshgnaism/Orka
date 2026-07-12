package com.Orka.SqlUtil.workflowDefinition;

import com.Orka.entities.definition.WorkflowDefinition;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
@Slf4j
public class WorkflowDefinitionsql {
    public static List<PreparedStatement> prepareSqlForInsertingWorkflowDefinition(Connection connection, WorkflowDefinition workflowDefinition){
        UUID id = workflowDefinition.getId();
        String name = workflowDefinition.getName();
        String description = workflowDefinition.getDescription();
        Integer version = workflowDefinition.getVersion();
        String created_by = workflowDefinition.getCreatorName();
        Date created_at = Date.valueOf(LocalDate.now());

        String sql = "INSERT INTO workflow_definition (id,name,description,version,created_by,created_at,start_state_definition_id,start_task_definition_id)" +
                "VALUES(?,?,?,?,?,?,?,?)";
        PreparedStatement psWorkflowInsert = null;
        try{
            assert connection != null;
            psWorkflowInsert = connection.prepareStatement(sql);
            psWorkflowInsert.setObject(1,id);
            psWorkflowInsert.setString(2,name);
            psWorkflowInsert.setString(3,description);
            psWorkflowInsert.setInt(4,version);
            psWorkflowInsert.setString(5,created_by);
            psWorkflowInsert.setDate(6,created_at);
            psWorkflowInsert.setObject(7,workflowDefinition.getStartStateDefinitionId());
            psWorkflowInsert.setObject(8,workflowDefinition.getStartTaskDefinitionId());
            psWorkflowInsert.addBatch();
        }catch (SQLException sqlException){
            log.error(sqlException.getMessage(),sqlException);
            log.error("[cannot get connection from database]");
            sqlException.printStackTrace();
        }
        assert psWorkflowInsert != null;
        return  List.of(psWorkflowInsert);
    };
}
