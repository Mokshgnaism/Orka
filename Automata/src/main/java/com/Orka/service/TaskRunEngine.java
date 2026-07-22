package com.Orka.service;

import com.Orka.entities.condition.AtomicCondition;
import com.Orka.entities.condition.EvaluationContext;
import com.Orka.entities.definition.StateDefinition;
import com.Orka.entities.definition.TaskDefinition;
import com.Orka.entities.definition.WorkflowDefinition;
import com.Orka.entities.runtime.StateRun;
import com.Orka.entities.runtime.TaskRun;
import com.Orka.entities.runtime.WorkflowRun;
import com.Orka.publisher.StateRunEventPublisher;
import com.Orka.repository.StateRunRepository;
import lombok.extern.slf4j.Slf4j;
import com.Orka.repository.TaskRunRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class TaskRunEngine {
    private final StateRunEventPublisher stateRunEventPublisher;
    private final StateRunEngine stateRunEngine;
    private final StateRunRepository stateRunRepository;
    private final TaskRunRepository taskRunRepository;

    public TaskRunEngine(StateRunEventPublisher stateRunEventPublisher, StateRunEngine stateRunEngine, StateRunRepository stateRunRepository, TaskRunRepository taskRunRepository) {
        this.stateRunEventPublisher = stateRunEventPublisher;
        this.stateRunEngine = stateRunEngine;
        this.stateRunRepository = stateRunRepository;
        this.taskRunRepository = taskRunRepository;
    }

    public  void update(TaskRun taskRun, WorkflowRun workflowRun,EvaluationContext evaluationContext ) {

        List<StateRun> stateRuns = taskRun.getStateRuns();
        List<StateRun> activeStateRuns = new ArrayList<>();
        if(taskRun.getStateRuns()==null){
            taskRun.setStateRuns(new ArrayList<>());
        }
        for (StateRun stateRun : stateRuns) {
            for(AtomicCondition atomicCondition : stateRun.getStateDefinition().getConditionToBecomeActive().getAtomicConditions()){
                log.info("[HUMAN] one of the condition for becoming active : {}",atomicCondition);
            }
            if (stateRun.getStateDefinition().getConditionToBecomeActive().isSatisified(evaluationContext)) {
                activeStateRuns.add(stateRun);
            }
        }
        if (activeStateRuns.isEmpty()) {
            log.info("No active state runs found for task {} ",taskRun.getTaskDefinitionName());
//            did not have any current state (default -> NOT_STARTED)
            return ;
        }
        activeStateRuns.sort(Comparator.comparingInt(x -> x.getStateDefinition().getPriority()));
        taskRun.setCurrentStateRun(activeStateRuns.getFirst());
        taskRun.setCurrentStateDefinitionName(activeStateRuns.getFirst().getStateDefinition().getName());

        log.info("Active state runs found for task {} : {}",taskRun.getTaskDefinitionName(),taskRun.getCurrentStateRun());
        StateRun activeStateRun = activeStateRuns.getFirst();
        stateRunEventPublisher.publishStateRunStartedEvent(activeStateRun);
    }

    public void setStart(WorkflowRun workflowRun){

        WorkflowDefinition workflowDefinition = workflowRun.getWorkflowDefinition();
        StateDefinition startStateDefinition = workflowDefinition.getStartState();
        UUID startTaskDefinitionId = workflowDefinition.getStartTaskDefinitionId();
        TaskRun startTask = taskRunRepository.findByWorkflowRunAndTaskDefinition_Id(workflowRun,startTaskDefinitionId);

        if(startTask==null){
            log.error("No start task found for task {}",startTaskDefinitionId);
        }
        startTask.setStartedAt(Instant.now());

        log.info("[SET START] for workflow {} start state has been set as [{}]",workflowRun.getWorkflowDefinition().getName(),startTask.getStartedAt());
        startTask.setCurrentState(workflowRun.getWorkflowDefinition().getStartState());
        StateRun stateRun = startTask.getStateRuns()
                .stream()
                .filter(sr -> startTask.getCurrentState().getId().equals(sr.getStateDefinition().getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Current state run not found"));
        startTask.setCurrentStateRun(stateRun);
        startTask.setCurrentStateDefinitionName(workflowRun.getWorkflowDefinition().getStartState().getName());
        log.info("[HUMAN] start state set with name = {}",startTask.getCurrentStateDefinitionName());
        taskRunRepository.save(startTask);
        log.info("[HUMAN] saved the start task after setting them");
        stateRunEventPublisher.publishStateRunStartedEvent(stateRun);
    }
}
