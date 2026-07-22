package com.Orka.Assembler;

import com.Orka.ENUM.status.TaskRunStatus;
import com.Orka.entities.definition.TaskDefinition;
import com.Orka.entities.definition.WorkflowDefinition;
import com.Orka.entities.runtime.StateRun;
import com.Orka.entities.runtime.TaskRun;
import com.Orka.entities.runtime.WorkflowRun;

import java.util.ArrayList;
import java.util.List;

public class TaskRunAssembler {
    public static TaskRun assemble(TaskDefinition taskDefinition, WorkflowRun workflowRun) {

        TaskRun taskRun = new TaskRun();

        taskRun.setWorkflowRun(workflowRun);


        taskRun.setTaskDefinition(taskDefinition);
        taskRun.setTaskDefinitionName(taskDefinition.getName());

        taskRun.setCurrentStateRun(null);
//        this is the stateDefinition;
        taskRun.setCurrentState(null);
        taskRun.setCurrentStateDefinitionName(null);

        taskRun.setRetryCount(0);

        taskRun.setStatus(TaskRunStatus.READY);

        List<StateRun> stateRuns = taskDefinition.getStates().stream().map(stateDefinition -> StateRunAssembler.assemble(taskDefinition,taskRun,stateDefinition)).toList();
        taskRun.setStateRuns(stateRuns);

        return taskRun;
    }
}
