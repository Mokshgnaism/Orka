package com.Orka.assembler.StateAssemblerUtil;

import com.Orka.entities.condition.AtomicCondition;
import com.Orka.entities.condition.ComparisonOperator;
import com.Orka.entities.condition.atomic.StateInCondition;
import com.Orka.entities.condition.atomic.StateInputCondition;
import com.Orka.entities.condition.atomic.StateOutputCondition;
import com.Orka.entities.condition.atomic.WorkflowVariableCondition;
import com.Orka.entities.datareference.DataReference;
import com.Orka.entities.datareference.StateInputReference;
import com.Orka.entities.datareference.StateOutputReference;
import com.Orka.util.JsonUtility;
import com.Orka.util.ProtoEnumMapper;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
@Slf4j
public class atomicConditionAssembler {
    public static AtomicCondition assemble(com.Orka.apiContract.generated.AtomicCondition atomicConditionDTO, UUID workflowDefinitionId) {
        if(atomicConditionDTO.hasStateIn()) {
            log.debug(
                    "[Assembled state input atomic condition]" +
                            "[name] : {} \n" +
                            "[workflowDefinitionId] : {} \n" +
                            "[taskDefinitionName] : {} \n]" +
                            "[stateDefinitionName] : {} \n]"
                    ,atomicConditionDTO.getName(),
                    workflowDefinitionId,
                    atomicConditionDTO.getStateIn().getDestinationTaskDefinitionName(),
                    atomicConditionDTO.getStateIn().getRequiredStateDefinitionName()
            );
            return StateInCondition.builder()
                    .name(atomicConditionDTO.getName())
                    .workflowDefinitionId(workflowDefinitionId)
                    .taskDefinitionName(atomicConditionDTO.getStateIn().getDestinationTaskDefinitionName())
                    .stateDefinitionName(atomicConditionDTO.getStateIn().getRequiredStateDefinitionName())
                    .build();
        }
        else if(atomicConditionDTO.hasStateInput()){
            String destinationTaskDefinitionName = atomicConditionDTO.getStateInput().getDestinationTaskDefinitionName();
            JsonNode expectedValue = JsonUtility.translateFromProtobufValue(atomicConditionDTO.getStateInput().getExpectedValue());
            ComparisonOperator comparisonOperator =
                    ProtoEnumMapper.toEntity(
                            atomicConditionDTO.getStateInput().getComparisonOperator(),
                            ComparisonOperator.class
                    );
            DataReference dataReference = DataReferenceAssembler.assemble(atomicConditionDTO.getStateInput().getDataReference());

            log.debug(
                    "[Assembled state input atomic condition]" +
                            "[name] : {} \n" +
                            "[workflowDefinitionId] : {} \n" +
                            "[reference] : {} \n]" +
                            "[operation] : {} \n]" +
                            "[expectedValue] : {} \n]"
                    ,atomicConditionDTO.getName(),
                    workflowDefinitionId,
                    (StateInputReference) dataReference,
                    comparisonOperator,
                    expectedValue
            );
            return StateInputCondition.builder()
                    .name(atomicConditionDTO.getName())
                    .workflowDefinitionId(workflowDefinitionId)
                    .reference((StateInputReference) dataReference)
                    .operator(comparisonOperator)
                    .expectedValue(expectedValue)
                    .build();
        }
        else if(atomicConditionDTO.hasStateOutput()){
            String destinationTaskDefinitionName = atomicConditionDTO.getStateOutput().getDestinationTaskDefinitionName();
            JsonNode expectedValue = JsonUtility.translateFromProtobufValue(atomicConditionDTO.getStateOutput().getExpectedValue());
            DataReference dataReference = DataReferenceAssembler.assemble(atomicConditionDTO.getStateOutput().getDataReference());
            ComparisonOperator comparisonOperator =
                    ProtoEnumMapper.toEntity(
                            atomicConditionDTO.getStateOutput().getComparisonOperator(),
                            ComparisonOperator.class
                    );
            log.debug(
                    "[Assembled state Output atomic condition]" +
                            "[name] : {} \n" +
                            "[workflowDefinitionId] : {} \n" +
                            "[reference] : {} \n]" +
                            "[operation] : {} \n]" +
                            "[expectedValue] : {} \n]"
                    ,atomicConditionDTO.getName(),
                    workflowDefinitionId,
                    (StateOutputReference) dataReference,
                    comparisonOperator,
                    expectedValue
            );
            return StateOutputCondition.builder()
                    .name(atomicConditionDTO.getName())
                    .workflowDefinitionId(workflowDefinitionId)
                    .reference((StateOutputReference) dataReference)
                    .expectedValue(expectedValue)
                    .operator(comparisonOperator)
                    .build();
        }
        else if (atomicConditionDTO.hasWorkflowVariable()) {
            ComparisonOperator comparisonOperator = ProtoEnumMapper.toEntity(
                    atomicConditionDTO.getWorkflowVariable().getComparisonOperator(),
                    ComparisonOperator.class
            );
            JsonNode expectedValue = JsonUtility.translateFromProtobufValue(atomicConditionDTO.getWorkflowVariable().getExpectedValue());

            log.debug(
                    "[Assembled state input atomic condition]" +
                            "[name] : {} \n" +
                            "[workflowDefinitionId] : {} \n" +
                            "[variable] : {} \n]" +
                            "[operation] : {} \n]" +
                            "[expectedValue] : {} \n]"
                    ,atomicConditionDTO.getName(),
                    workflowDefinitionId,
                    atomicConditionDTO.getWorkflowVariable().getWorkflowVariableName(),
                    comparisonOperator,
                    expectedValue
            );

            return WorkflowVariableCondition.builder()
                    .name(atomicConditionDTO.getName())
                    .workflowDefinitionId(workflowDefinitionId)
                    .variableName(atomicConditionDTO.getWorkflowVariable().getWorkflowVariableName())
                    .operator(comparisonOperator)
                    .expectedValue(expectedValue)
                    .build();

        }
        return null;
    }
}
