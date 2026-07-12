package com.Orka.Repository;
import com.Orka.SqlUtil.workflowDefinition.TaskDefinitionSql;
import com.Orka.SqlUtil.workflowDefinition.VariableSql;
import com.Orka.SqlUtil.workflowDefinition.WorkflowDefinitionsql;
import com.Orka.entities.definition.WorkflowDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
@Slf4j
@Repository
public class jdbcWorkflowDefinitionRepository implements WorkflowDefinitionRepository {
    private final DataSource dataSource;

    public jdbcWorkflowDefinitionRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void save(WorkflowDefinition workflowDefinition){
        Connection connection = null;
        try{
            connection  = dataSource.getConnection();
        }catch (SQLException sqlException){
            log.error(sqlException.getMessage(),sqlException);
            log.error("[cannot get connection from database]");
        }
        assert connection != null;
        List<PreparedStatement> psWorkflowDefinitionInsert = WorkflowDefinitionsql.prepareSqlForInsertingWorkflowDefinition(connection,workflowDefinition);
        List<PreparedStatement> psVariableInsert = VariableSql.prepareSqlForInsertingVariables(connection,workflowDefinition);
        List<PreparedStatement> psTaskDefinitionInsert = TaskDefinitionSql.prepareSqlForInsertingTaskDefinition(connection,workflowDefinition);

        List<PreparedStatement>all = new ArrayList<>();
        all.addAll(psWorkflowDefinitionInsert);
        all.addAll(psVariableInsert);
        all.addAll(psTaskDefinitionInsert);
        boolean success = false;
        try{
            for(PreparedStatement ps : psWorkflowDefinitionInsert){
                ps.executeBatch();
            }
            for(PreparedStatement ps : psVariableInsert){
                ps.executeBatch();
            }
            for(PreparedStatement ps : psTaskDefinitionInsert){
                ps.executeBatch();
            }
            success = true;
            connection.commit();
        }catch (SQLException sqlException){
            log.error(sqlException.getMessage(),sqlException);
            log.error("[writing to database failed ]");
            try {
                connection.rollback();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if(success){
            log.info("[workflow definition saved ] {} :  {}",workflowDefinition.getId(),workflowDefinition.getName());
        }
        else{
            log.error("[workflow definition save failed ]");
        }
    }
}
