package com.Orka.assembler;

import com.Orka.ENUM.typeEnums.ORKA_INTERNAL_STATE;
import com.Orka.apiContract.generated.OrkaInternalState;
import com.Orka.assembler.UtilityDefinitionAssemblers.*;
import com.Orka.entities.condition.Condition;
import com.Orka.entities.definition.*;
import com.Orka.assembler.StateAssemblerUtil.*;
import com.Orka.internal.VariableDefinition;
import com.Orka.util.ProtoEnumMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;
@Slf4j
public class StateDefinitionAssembler {
    public static StateDefinition assemble(com.Orka.apiContract.generated.StateDefinition stateDefinition, UUID taskDefinitionId, UUID workflowDefinitionId, List<VariableDefinition>variableDefinitions) {
        UUID stateDefinitionId = UUID.randomUUID();
        String stateDefinitionName = stateDefinition.getName();
        Integer stateDefinitionPriority = stateDefinition.getPriority();
        ORKA_INTERNAL_STATE internalState = ProtoEnumMapper.toEntity(stateDefinition.getInternalState(),ORKA_INTERNAL_STATE.class);

        Condition condition = ConditionAssembler.assemble(stateDefinition.getCondition(),workflowDefinitionId);

        InputDefinition inputDefinition = InputDefinitionAssembler.assemble(stateDefinition.getInputDefinition());

        OutputDefinition outputDefinition = OutputDefinitionAssembler.assemble(stateDefinition.getOutputDefinition());

        ScriptDefinition scriptDefinition = ScriptDefinitionAssembler.assemble(stateDefinition.getScriptDefinition(),workflowDefinitionId,stateDefinitionId,variableDefinitions);


           StateDefinition state =  StateDefinition.builder()
                .name(stateDefinitionName)
                .priority(stateDefinitionPriority)
                .conditionToBecomeActive(condition)
                .inputDefinition(inputDefinition)
                .outputDefinition(outputDefinition)
                .scriptDefinition(scriptDefinition)
                   .internalState(internalState)
                   .taskDefinitionId(taskDefinitionId)
                .build();

        log.debug(
                """
                State:
                  name={}
                  priority={}
                  internalState={}
                """,
                state.getName(),
                state.getPriority(),
                state.getInternalState()
        );
        log.debug("Condition = {}", state.getConditionToBecomeActive());

        log.debug("InputDefinition = {}", state.getInputDefinition());

        log.debug("OutputDefinition = {}", state.getOutputDefinition());

        log.debug("Script = {}", state.getScriptDefinition());
        if(state.getScriptDefinition()!=null){
            state.getScriptDefinition().setStateDefinition(state);
        }
        return state;
    }
//    TODO: remove all the mappers and use consistent enums generated from the proto files and remove enums from all the files and let it be generated from protos .
    private static  ORKA_INTERNAL_STATE ENUM_MAPPER(com.Orka.apiContract.generated.OrkaInternalState DTO_STATE) {
        return ProtoEnumMapper.toEntity(DTO_STATE,ORKA_INTERNAL_STATE.class);
    }
}
