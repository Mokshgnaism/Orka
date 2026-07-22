package com.Orka.assembler.utilResolver;

import com.Orka.entities.bindings.InputBinding;
import com.Orka.entities.condition.AtomicCondition;
import com.Orka.entities.condition.Condition;
import com.Orka.entities.condition.atomic.StateInputCondition;
import com.Orka.entities.condition.atomic.StateOutputCondition;
import com.Orka.entities.condition.atomic.WorkflowVariableCondition;
import com.Orka.entities.datareference.DataReference;
import com.Orka.entities.datareference.StateInputReference;
import com.Orka.entities.datareference.StateOutputReference;
import com.Orka.entities.definition.InputDefinition;
import com.Orka.entities.definition.StateDefinition;
import com.Orka.entities.definition.TaskDefinition;
import com.Orka.entities.definition.WorkflowDefinition;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public final class WorkflowReferenceResolver {

    private WorkflowReferenceResolver() {
    }

    public static void resolve(WorkflowDefinition workflowDefinition) {

        log.debug("Resolving workflow references");

        Map<String, UUID> taskNameToId = new HashMap<>();
        Map<String, UUID> variableNameToId = new HashMap<>();
        Map<String, StateDefinition> stateByKey = new HashMap<>();

        //----------------------------------------------------------
        // Build lookup tables
        //----------------------------------------------------------

        for (TaskDefinition task : workflowDefinition.getTasks()) {

            taskNameToId.put(task.getName(), task.getId());

            for (StateDefinition state : task.getStates()) {

                stateByKey.put(
                        buildStateKey(task.getName(), state.getName()),
                        state
                );
            }
        }

        workflowDefinition.getVariableDefinitions().forEach(variable ->
                variableNameToId.put(
                        variable.getName(),
                        variable.getId()
                )
        );

        //----------------------------------------------------------
        // Resolve references
        //----------------------------------------------------------

        for (TaskDefinition task : workflowDefinition.getTasks()) {

            for (StateDefinition state : task.getStates()) {

                resolveCondition(
                        state.getConditionToBecomeActive(),
                        taskNameToId,
                        stateByKey,
                        variableNameToId
                );

                resolveInputDefinition(
                        state.getInputDefinition(),
                        taskNameToId,
                        stateByKey
                );
            }
        }

        log.debug("Workflow reference resolution completed.");
    }

    //----------------------------------------------------------
    // Condition Resolution
    //----------------------------------------------------------

    private static void resolveCondition(
            Condition condition,
            Map<String, UUID> taskIds,
            Map<String, StateDefinition> stateByKey,
            Map<String, UUID> variableIds
    ) {

        if (condition == null)
            return;

        for (AtomicCondition atomicCondition : condition.getAtomicConditions()) {

            if (atomicCondition instanceof StateInputCondition stateInputCondition) {

                resolveReference(
                        stateInputCondition.getReference(),
                        taskIds,
                        stateByKey
                );
            }

            else if (atomicCondition instanceof StateOutputCondition stateOutputCondition) {

                resolveReference(
                        stateOutputCondition.getReference(),
                        taskIds,
                        stateByKey
                );
            }

            else if (atomicCondition instanceof WorkflowVariableCondition workflowVariableCondition) {

                workflowVariableCondition.setVariableDefinitionId(
                        variableIds.get(
                                workflowVariableCondition.getVariableName()
                        )
                );
            }
        }
    }

    //----------------------------------------------------------
    // Input Definition Resolution
    //----------------------------------------------------------

    private static void resolveInputDefinition(
            InputDefinition inputDefinition,
            Map<String, UUID> taskIds,
            Map<String, StateDefinition> stateByKey
    ) {

        if (inputDefinition == null)
            return;

        for (InputBinding inputBinding : inputDefinition.getBindings()) {

            resolveReference(
                    inputBinding.getSource(),
                    taskIds,
                    stateByKey
            );
        }
    }

    //----------------------------------------------------------
    // Data Reference Resolution
    //----------------------------------------------------------

    private static void resolveReference(
            DataReference reference,
            Map<String, UUID> taskIds,
            Map<String, StateDefinition> stateByKey
    ) {

        if (reference == null)
            return;

        if (reference instanceof StateInputReference stateInputReference) {

            fillStateInputReference(
                    stateInputReference,
                    taskIds,
                    stateByKey
            );
        }

        else if (reference instanceof StateOutputReference stateOutputReference) {

            fillStateOutputReference(
                    stateOutputReference,
                    taskIds,
                    stateByKey
            );
        }
    }

    //----------------------------------------------------------
    // Fill StateInputReference
    //----------------------------------------------------------

    private static void fillStateInputReference(
            StateInputReference reference,
            Map<String, UUID> taskIds,
            Map<String, StateDefinition> stateByKey
    ) {

        reference.setTaskDefinitionId(
                taskIds.get(reference.getTaskDefinitionName())
        );

        reference.setStateDefinition(
                stateByKey.get(
                        buildStateKey(
                                reference.getTaskDefinitionName(),
                                reference.getStateDefinitionName()
                        )
                )
        );
    }

    //----------------------------------------------------------
    // Fill StateOutputReference
    //----------------------------------------------------------

    private static void fillStateOutputReference(
            StateOutputReference reference,
            Map<String, UUID> taskIds,
            Map<String, StateDefinition> stateByKey
    ) {

        reference.setTaskDefinitionId(
                taskIds.get(reference.getTaskDefinitionName())
        );

        reference.setStateDefinition(
                stateByKey.get(
                        buildStateKey(
                                reference.getTaskDefinitionName(),
                                reference.getStateDefinitionName()
                        )
                )
        );
    }

    //----------------------------------------------------------
    // Utility
    //----------------------------------------------------------

    private static String buildStateKey(
            String taskName,
            String stateName
    ) {
        return taskName + "::" + stateName;
    }
}