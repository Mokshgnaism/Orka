package com.Orka.SqlUtil.workflowDefinition;

import com.Orka.entities.definition.WorkflowDefinition;
import com.Orka.internal.VariableDefinition;
import com.Orka.util.JsonUtility;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

@Slf4j
public class VariableSql {
    public static List<PreparedStatement> prepareSqlForInsertingVariables(
            java.sql.Connection connection,
            WorkflowDefinition workflowDefinition
    ){

        String sql = """
        INSERT INTO variable_definition (
            id,
            workflow_definition_id,
            name,
            variable_type,
            default_value
        )
        VALUES (?,?,?,CAST(? AS variable_type),?)
        """;
        PreparedStatement ps = null;
        try{
             ps = connection.prepareStatement(sql);

            for (VariableDefinition varDef : workflowDefinition.getVariableDefinitions()) {
                ps.setObject(1, varDef.getId());
                ps.setObject(2, workflowDefinition.getId());
                ps.setString(3, varDef.getName());
                ps.setString(4, varDef.getType().name());
                if (varDef.getDefaultValue() == null) {
                    ps.setNull(5, Types.OTHER);
                } else {
                    JsonNode node = varDef.getDefaultValue();
                    ps.setObject(5, JsonUtility.toJson(node));
                }
                ps.addBatch();
            }
        } catch (SQLException sqlException) {
                log.error(sqlException.getMessage(), sqlException);
                log.error("unable to prepare statement for VARIABLE insertion");
                sqlException.printStackTrace();
        }
        assert ps!=null;
        return List.of(ps);
    }
}
