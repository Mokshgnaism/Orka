package com.Orka.SqlUtil.workflowDefinition;

import com.Orka.entities.condition.AtomicCondition;
import com.Orka.entities.condition.Condition;
import com.Orka.entities.condition.atomic.StateInCondition;
import com.Orka.entities.condition.atomic.StateInputCondition;
import com.Orka.entities.condition.atomic.StateOutputCondition;
import com.Orka.entities.condition.atomic.WorkflowVariableCondition;
import com.Orka.entities.datareference.ConstantReference;
import com.Orka.entities.datareference.StateInputReference;
import com.Orka.entities.datareference.StateOutputReference;
import com.Orka.entities.datareference.WorkflowVariableReference;
import com.Orka.entities.definition.WorkflowDefinition;
import com.Orka.util.JsonUtility;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Slf4j
public class StateDefinitionSql {

    public static List<PreparedStatement> prepareSqlForInsertingStateDefinition(
            Connection connection,
            WorkflowDefinition workflowDefinition
    ) {

        List<PreparedStatement> preparedStatements = new ArrayList<>();

        //----------------------------------------------------
        // Build Lookup Maps
        //----------------------------------------------------

        Map<String, UUID> taskNameToId = new HashMap<>();

        Map<String, UUID> variableNameToId = new HashMap<>();

        Map<String, UUID> stateNameToId = new HashMap<>();

        workflowDefinition.getVariableDefinitions().forEach(v ->
                variableNameToId.put(v.getName(), v.getId()));

        workflowDefinition.getTasks().forEach(task -> {

            taskNameToId.put(task.getName(), task.getId());

            task.getStates().forEach(state ->

                    stateNameToId.put(
                            task.getName() + "::" + state.getName(),
                            state.getId()
                    )
            );
        });

        //----------------------------------------------------
        // SQL
        //----------------------------------------------------

//        TODO : correctly map internal states at assembler level and remove this stub .
        String sqlStateDefinition =
                """
                INSERT INTO state_definition
                (id,task_definition_id,name,priority,internal_state)
                VALUES (?,?,?,?,CAST(? AS orka_internal_state))
                """;

        String sqlCondition =
                """
                INSERT INTO condition_definition
                (id,workflow_definition_id,name,expression)
                VALUES (?,?,?,?)
                """;

        String sqlAtomicCondition =
                """
                INSERT INTO atomic_condition
                (id,workflow_definition_id,name,type)
                VALUES (?,?,?,CAST(? AS atomic_condition_type))
                """;

        String sqlJoinConditionAtomic =
                """
                INSERT INTO condition_atomic_condition
                (condition_definition_id,atomic_condition_id)
                VALUES (?,?)
                """;

        String sqlJoinStateCondition =
                """
                INSERT INTO state_definition_condition
                (state_definition_id,condition_definition_id)
                VALUES (?,?)
                """;

        String sqlStateInputCondition =
                """
                INSERT INTO state_input_condition
                (
                    atomic_condition_id,
                    task_definition_id,
                    state_definition_id,
                    json_path,
                    comparison_operator,
                    expected_value
                )
                VALUES (?,?,?,?,CAST(? AS comparison_operator),?)
                """;

        String sqlStateOutputCondition =
                """
                INSERT INTO state_output_condition
                (
                    atomic_condition_id,
                    task_definition_id,
                    state_definition_id,
                    json_path,
                    comparison_operator,
                    expected_value
                )
                VALUES (?,?,?,?,CAST(? AS comparison_operator),?)
                """;

        String sqlWorkflowVariableCondition =
                """
                INSERT INTO workflow_variable_condition
                (
                    atomic_condition_id,
                    variable_definition_id,
                    comparison_operator,
                    expected_value
                )
                VALUES (?,?,CAST(? AS comparison_operator),?)
                """;

        String sqlStateInCondition =
                """
                INSERT INTO state_in_condition
                (
                    atomic_condition_id,
                    task_definition_id,
                    state_definition_id
                )
                VALUES (?,?,?)
                """;

        String sqlInputDefinition = """
                INSERT INTO input_definition
                (id,state_definition_id,json_schema)
                VALUES (?,?,?)              \s
               \s""";

        String sqlOutputDefinition = """
                INSERT INTO output_definition
                (id,state_definition_id,json_schema)
                VALUES (?,?,?)   \s
                """;

        String sqlInputBinding = """
                INSERT into input_binding
                (id,input_definition_id,destination_json_path,data_reference_id)
                VALUES (?,?,?,?)
                """;

        String sqlDataReference = """
                INSERT INTO data_reference
                (id,type,task_definition_id,state_definition_id,json_path,variable_definition_id,constant_value)
                VALUES(?,CAST(? AS data_reference_type),?,?,?,?,CAST( ? AS JSONB))
                """;
        String sqlScriptDefinition = """
                INSERT INTO script_definition
                (id,state_definition_id,script_name,docker_image,command_template,timeout_ms)
                VALUES (?,?,?,?,?,?)
                """;

        try {

            PreparedStatement psState =
                    connection.prepareStatement(sqlStateDefinition);

            PreparedStatement psCondition =
                    connection.prepareStatement(sqlCondition);

            PreparedStatement psAtomic =
                    connection.prepareStatement(sqlAtomicCondition);

            PreparedStatement psJoinConditionAtomic =
                    connection.prepareStatement(sqlJoinConditionAtomic);

            PreparedStatement psJoinStateCondition =
                    connection.prepareStatement(sqlJoinStateCondition);

            PreparedStatement psStateInput =
                    connection.prepareStatement(sqlStateInputCondition);

            PreparedStatement psStateOutput =
                    connection.prepareStatement(sqlStateOutputCondition);

            PreparedStatement psWorkflowVariable =
                    connection.prepareStatement(sqlWorkflowVariableCondition);

            PreparedStatement psStateIn =
                    connection.prepareStatement(sqlStateInCondition);
            PreparedStatement psInputDefinition =
                    connection.prepareStatement(sqlInputDefinition);

            PreparedStatement psOutputDefinition =
                    connection.prepareStatement(sqlOutputDefinition);

            PreparedStatement psInputBinding =
                    connection.prepareStatement(sqlInputBinding);

            PreparedStatement psDataReference =
                    connection.prepareStatement(sqlDataReference);

            PreparedStatement psScriptDefinition =
                    connection.prepareStatement(sqlScriptDefinition);

            //----------------------------------------------------
            // Walk Workflow
            //----------------------------------------------------

            for (var task : workflowDefinition.getTasks()) {

                for (var state : task.getStates()) {

                    //---------------------------------------------
                    // State Definition
                    //---------------------------------------------

                    psState.setObject(1, state.getId());
                    psState.setObject(2, state.getTaskDefinitionId());
                    psState.setString(3, state.getName());
                    psState.setInt(4, state.getPriority());
                    psState.setString(5, state.getInternalState().name());

                    psState.addBatch();

                    //---------------------------------------------
                    // Condition
                    //---------------------------------------------

                    Condition condition =
                            state.getConditionToBecomeActive();

                    UUID conditionId = UUID.randomUUID();

                    psCondition.setObject(1, conditionId);
                    psCondition.setObject(2, workflowDefinition.getId());
                    psCondition.setString(3, condition.getName());
                    psCondition.setString(4, condition.getExpression());

                    psCondition.addBatch();

                    //---------------------------------------------
                    // Join State <-> Condition
                    //---------------------------------------------

                    psJoinStateCondition.setObject(1, state.getId());
                    psJoinStateCondition.setObject(2, conditionId);

                    psJoinStateCondition.addBatch();

                    //----------------------------------------------------
// Input Definition
//----------------------------------------------------

                    if (state.getInputDefinition() != null) {

                        psInputDefinition.setObject(
                                1,
                                state.getInputDefinition().getId());

                        psInputDefinition.setObject(
                                2,
                                state.getId());

                        psInputDefinition.setString(
                                3,
                                state.getInputDefinition().getJsonSchema());

                        psInputDefinition.addBatch();

                        //------------------------------------------------
                        // Input Bindings
                        //------------------------------------------------

                        for (var binding : state.getInputDefinition().getBindings()) {

                            UUID dataReferenceId = UUID.randomUUID();

                            psInputBinding.setObject(
                                    1,
                                    binding.getId());

                            psInputBinding.setObject(
                                    2,
                                    state.getInputDefinition().getId());

                            psInputBinding.setString(
                                    3,
                                    binding.getDestinationJsonPath());

                            psInputBinding.setObject(
                                    4,
                                    dataReferenceId);

                            psInputBinding.addBatch();

                            //--------------------------------------------
                            // Data Reference
                            //--------------------------------------------

                            psDataReference.setObject(
                                    1,
                                    dataReferenceId);

                            if (binding.getSource() instanceof StateInputReference ref) {

                                psDataReference.setString(
                                        2,
                                        "STATE_INPUT");

                                psDataReference.setObject(
                                        3,
                                        ref.getTaskDefinitionId());

                                psDataReference.setObject(
                                        4,
                                        ref.getStateDefinitionId());

                                psDataReference.setString(
                                        5,
                                        ref.getJsonPath());

                                psDataReference.setObject(
                                        6,
                                        null);

                                psDataReference.setObject(
                                        7,
                                        null);

                            }

                            else if (binding.getSource() instanceof StateOutputReference ref) {

                                psDataReference.setString(
                                        2,
                                        "STATE_OUTPUT");

                                psDataReference.setObject(
                                        3,
                                        ref.getTaskDefinitionId());

                                psDataReference.setObject(
                                        4,
                                        ref.getStateDefinitionId());

                                psDataReference.setString(
                                        5,
                                        ref.getJsonPath());

                                psDataReference.setObject(
                                        6,
                                        null);

                                psDataReference.setObject(
                                        7,
                                        null);

                            }

                            else if (binding.getSource() instanceof WorkflowVariableReference ref) {

                                psDataReference.setString(
                                        2,
                                        "WORKFLOW_VARIABLE");

                                psDataReference.setObject(
                                        3,
                                        null);

                                psDataReference.setObject(
                                        4,
                                        null);

                                psDataReference.setString(
                                        5,
                                        null);

                                psDataReference.setObject(
                                        6,
                                        variableNameToId.get(ref.getVariableName())
                                        );

                                psDataReference.setObject(
                                        7,
                                        null);

                            }

                            else if (binding.getSource() instanceof ConstantReference ref) {

                                psDataReference.setString(
                                        2,
                                        "CONSTANT");

                                psDataReference.setObject(
                                        3,
                                        null);

                                psDataReference.setObject(
                                        4,
                                        null);

                                psDataReference.setString(
                                        5,
                                        null);

                                psDataReference.setObject(
                                        6,
                                        null);

                                psDataReference.setObject(
                                        7,
                                        JsonUtility.toJson(ref.getJsonValue()));

                            }

                            psDataReference.addBatch();
                        }
                    }

                    //----------------------------------------------------
// Output Definition
//----------------------------------------------------

                    if (state.getOutputDefinition() != null) {

                        psOutputDefinition.setObject(
                                1,
                                state.getOutputDefinition().getId());

                        psOutputDefinition.setObject(
                                2,
                                state.getId());

                        psOutputDefinition.setString(
                                3,
                                state.getOutputDefinition().getJsonSchema());

                        psOutputDefinition.addBatch();
                    }



                    //----------------------------------------------------
// Script Definition
//----------------------------------------------------

                    if (state.getScriptDefinition() != null) {

                        psScriptDefinition.setObject(
                                1,
                                state.getScriptDefinition().getId());

                        psScriptDefinition.setObject(
                                2,
                                state.getId());

                        psScriptDefinition.setString(
                                3,
                                state.getScriptDefinition().getScriptName());

                        psScriptDefinition.setString(
                                4,
                                state.getScriptDefinition().getDockerImage());

                        psScriptDefinition.setString(
                                5,
                                state.getScriptDefinition().getEntryCommand());

                        psScriptDefinition.setInt(
                                6,
                                state.getScriptDefinition().getTimeout());

                        psScriptDefinition.addBatch();
                    }



                    //---------------------------------------------
                    // Atomic Conditions
                    //---------------------------------------------

                    for (AtomicCondition atomic :
                            condition.getAtomicConditions()) {

                        UUID atomicConditionId =
                                UUID.randomUUID();

                        psAtomic.setObject(1, atomicConditionId);
                        psAtomic.setObject(
                                2,
                                workflowDefinition.getId());

                        psAtomic.setString(
                                3,
                                atomic.getName());

                        psJoinConditionAtomic.setObject(
                                1,
                                conditionId);

                        psJoinConditionAtomic.setObject(
                                2,
                                atomicConditionId);

                        psJoinConditionAtomic.addBatch();

                        //-----------------------------------------
                        // STATE INPUT CONDITION
                        //-----------------------------------------

                        if (atomic instanceof StateInputCondition sic) {

                            psAtomic.setString(4, "STATE_INPUT");
                            psAtomic.addBatch();

                            StateInputReference reference =
                                    sic.getReference();

                            psStateInput.setObject(
                                    1,
                                    atomicConditionId);

                            psStateInput.setObject(
                                    2,
                                    reference.getTaskDefinitionId());

                            psStateInput.setObject(
                                    3,
                                    reference.getStateDefinitionId());

                            psStateInput.setString(
                                    4,
                                    reference.getJsonPath());

                            psStateInput.setString(
                                    5,
                                    sic.getOperator().name());

                            psStateInput.setObject(
                                    6,
                                    JsonUtility.toJson( sic.getExpectedValue()));

                            psStateInput.addBatch();
                        }

                        //-----------------------------------------
                        // STATE OUTPUT CONDITION
                        //-----------------------------------------

                        else if (atomic instanceof StateOutputCondition soc) {

                            psAtomic.setString(4, "STATE_OUTPUT");
                            psAtomic.addBatch();

                            StateOutputReference reference =
                                    soc.getReference();

                            psStateOutput.setObject(
                                    1,
                                    atomicConditionId);

                            psStateOutput.setObject(
                                    2,
                                    reference.getTaskDefinitionId());

                            psStateOutput.setObject(
                                    3,
                                    reference.getStateDefinitionId());

                            psStateOutput.setString(
                                    4,
                                    reference.getJsonPath());

                            psStateOutput.setString(
                                    5,
                                    soc.getOperator().name());

                            psStateOutput.setObject(
                                    6,
                                   JsonUtility.toJson( soc.getExpectedValue()));
                            psStateOutput.addBatch();
                        }

                        //-----------------------------------------
                        // WORKFLOW VARIABLE CONDITION
                        //-----------------------------------------

                        else if (atomic instanceof WorkflowVariableCondition wvc) {

                            psAtomic.setString(4, "WORKFLOW_VARIABLE");
                            psAtomic.addBatch();

                            psWorkflowVariable.setObject(
                                    1,
                                    atomicConditionId);

                            psWorkflowVariable.setObject(
                                    2,
                                    wvc.getVariableDefinitionId());

                            psWorkflowVariable.setString(
                                    3,
                                    wvc.getOperator().name());

                            psWorkflowVariable.setObject(
                                    4,
                                   JsonUtility.toJson( wvc.getExpectedValue()));

                            psWorkflowVariable.addBatch();
                        }

                        //-----------------------------------------
                        // STATE IN CONDITION
                        //-----------------------------------------

                        else if (atomic instanceof StateInCondition sic) {

                            psAtomic.setString(4, "STATE_IN");
                            psAtomic.addBatch();

                            psStateIn.setObject(
                                    1,
                                    atomicConditionId);

                            psStateIn.setObject(
                                    2,
                                    taskNameToId.get(
                                            sic.getTaskDefinitionName()));

                            psStateIn.setObject(
                                    3,
                                    stateNameToId.get(
                                            sic.getTaskDefinitionName()
                                                    + "::"
                                                    + sic.getStateDefinitionName()));

                            psStateIn.addBatch();
                        }
                    }
                }
            }

            //----------------------------------------------------
            // FK Order
            //----------------------------------------------------

            preparedStatements.add(psState);

            preparedStatements.add(psCondition);

            preparedStatements.add(psAtomic);

            preparedStatements.add(psJoinConditionAtomic);

            preparedStatements.add(psJoinStateCondition);

            preparedStatements.add(psStateInput);

            preparedStatements.add(psStateOutput);

            preparedStatements.add(psWorkflowVariable);

            preparedStatements.add(psStateIn);

            preparedStatements.add(psInputDefinition);

            preparedStatements.add(psOutputDefinition);

            preparedStatements.add(psInputBinding);

            preparedStatements.add(psDataReference);

            preparedStatements.add(psScriptDefinition);

        } catch (SQLException ex) {

            log.error(
                    "Failed while preparing StateDefinition SQL",
                    ex);

            throw new RuntimeException(ex);
        }

        return preparedStatements;
    }

}