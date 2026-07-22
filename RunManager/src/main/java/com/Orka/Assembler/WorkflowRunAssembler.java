package com.Orka.Assembler;
import com.Orka.ENUM.AuthEnums.TASK_RUN_AUTH_ROLE;
import com.Orka.ENUM.status.WorkflowRunStatus;
import com.Orka.apiContract.generated.TaskRunAuthRole;
import com.Orka.apiContract.generated.TotalAuthForOneTaskRun;
import com.Orka.apiContract.generated.WorkflowRunAuthorization;
import com.Orka.entities.authorization.TaskRunAuthorization;
import com.Orka.entities.definition.WorkflowDefinition;
import com.Orka.entities.runtime.TaskRun;
import com.Orka.entities.runtime.WorkflowRun;
import com.Orka.util.ProtoEnumMapper;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkflowRunAssembler {
    public static WorkflowRun assemble(WorkflowDefinition workflowDefinition, List<WorkflowRunAuthorization> workflowRunAuthorizationDTOS, List<TotalAuthForOneTaskRun>tasRunAuthorizationDTOS) {
        WorkflowRun workflowRun = new WorkflowRun();

//        ---------------------- setting Workflow definition------------------------------
        workflowRun.setWorkflowDefinition(workflowDefinition);
//        --------------------------------------------------------------------------------

        workflowRun.setStatus(WorkflowRunStatus.CREATED);

        workflowRun.setStartedAt(Instant.now());

        workflowRun.setCompletedAt(null);

//        assemble the task runs first

        List<TaskRun>taskRuns = workflowDefinition.
                getTasks().
                stream().
                map(taskDefinition -> TaskRunAssembler.assemble(taskDefinition, workflowRun)).
                toList();

        List<com.Orka.entities.authorization.WorkflowRunAuthorization> authorizations = workflowRunAuthorizationDTOS.stream().map(auth->WorkflowRunAuthorizationAssembler.assemble(auth,workflowRun)).toList();

        workflowRun.setAuthorizations(authorizations);

        Map<String,TaskRun>nameToTaskRun = new HashMap<>();
        for(TaskRun taskRun : taskRuns){
            nameToTaskRun.put(taskRun.getTaskDefinition().getName(), taskRun);
        }
        for (TaskRun taskRun : taskRuns) {
            System.out.println(taskRun.getTaskDefinitionName());
        }

        for(var taskRunAuth:tasRunAuthorizationDTOS){
            String taskName = taskRunAuth.getName();
            TaskRun taskRun = nameToTaskRun.get(taskName);
            List<TaskRunAuthorization> authorizationList = taskRunAuth.getTaskRunAuthorizationsList().stream().map(taskRunAuthDTO-> {

                TaskRunAuthorization taskRunAuthorization = new TaskRunAuthorization();
                taskRunAuthorization.setTaskRun(taskRun);
                taskRunAuthorization.setUsername(taskRunAuthDTO.getUsername());
                taskRunAuthorization.setAuthRole(TASK_RUN_AUTH_MAPPER(taskRunAuthDTO.getAuthRole()));

                return  taskRunAuthorization;
            }).toList();
//            TODO : check a validation (if the user sends a wrong task name then we will be fucked .
            taskRun.setAuthorizations(authorizationList);
        }

        workflowRun.setTaskRuns(taskRuns);

        List<com.Orka.internal.Variable>variables = workflowDefinition.getVariableDefinitions().stream().map(varDef->VariableAssembler.assemble(varDef,workflowRun)).toList();

        workflowRun.setVariables(variables);

        return workflowRun;
    }
    private static TASK_RUN_AUTH_ROLE TASK_RUN_AUTH_MAPPER(TaskRunAuthRole taskRunAuthRole){
        if(taskRunAuthRole.equals(TaskRunAuthRole.TASK_RUN_ASSIGNEE)){
            return TASK_RUN_AUTH_ROLE.ASSIGNEE;
        }else if(taskRunAuthRole.equals(TaskRunAuthRole.TASK_RUN_VIEWER)){
            return TASK_RUN_AUTH_ROLE.VIEWER;
        }else
            return null;
    }
}
