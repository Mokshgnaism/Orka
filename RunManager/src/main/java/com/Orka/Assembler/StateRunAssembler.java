package com.Orka.Assembler;

import com.Orka.ENUM.status.StateRunStatus;
import com.Orka.entities.definition.StateDefinition;
import com.Orka.entities.definition.TaskDefinition;
import com.Orka.entities.runtime.StateRun;
import com.Orka.entities.runtime.TaskRun;

public class StateRunAssembler {
    public static StateRun assemble(TaskDefinition taskDefinition, TaskRun taskRun, StateDefinition stateDefinition) {
        StateRun stateRun = new StateRun();
        stateRun.setTaskRun(taskRun);
        stateRun.setStateDefinition(stateDefinition);
        stateRun.setStatus(StateRunStatus.WAITING);
        stateRun.setInput(null);
        stateRun.setOutput(null);
        return stateRun;
    }
}
