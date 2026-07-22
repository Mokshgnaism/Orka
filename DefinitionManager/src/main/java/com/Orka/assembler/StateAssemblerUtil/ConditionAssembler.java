package com.Orka.assembler.StateAssemblerUtil;

import com.Orka.apiContract.generated.Condition;
import com.Orka.entities.condition.AtomicCondition;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Slf4j
public class ConditionAssembler {
    public static com.Orka.entities.condition.Condition assemble(Condition conditionDTO, UUID workflowDefinitionId){
        String expression = conditionDTO.getExpression();
        String conditionName = conditionDTO.getConditionName();
        List<com.Orka.apiContract.generated.AtomicCondition> atomicConditionDTOS = conditionDTO.getAtomicConditionsList();

        List<AtomicCondition> atomicConditions = atomicConditionDTOS.stream().map(atomicConditionDTO -> atomicConditionAssembler.assemble(atomicConditionDTO,workflowDefinitionId)).toList();
        log.debug("[CREATED] Condition : {} ",conditionName);
        log.debug("[CREATED] conditionExpression : {} ",expression);
        log.debug("[CREATED] Condition : {} ",atomicConditions);

        var condition = com.Orka.entities.condition.Condition.builder()
                .atomicConditions(atomicConditions)
                .expression(expression)
                .name(conditionName)
                .build();
        condition.getAtomicConditions().forEach(atomicCondition -> atomicCondition.setCondition(condition));
        return condition;
    }
}
