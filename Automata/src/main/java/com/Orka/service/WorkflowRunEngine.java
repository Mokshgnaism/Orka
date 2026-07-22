package com.Orka.service;

import com.Orka.entities.condition.EvaluationContext;
import com.Orka.entities.definition.WorkflowDefinition;
import com.Orka.entities.runtime.TaskRun;
import com.Orka.entities.runtime.WorkflowRun;
import com.Orka.repository.WorkflowRunRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
@Service
@Slf4j
@Transactional
public class WorkflowRunEngine {
    private final WorkflowRunRepository workflowRunRepository;
    private final TaskRunEngine taskRunEngine;
    private final StateRunEngine stateRunEngine;

    public WorkflowRunEngine(WorkflowRunRepository workflowRunRepository, TaskRunEngine taskRunEngine, StateRunEngine stateRunEngine) {
        this.workflowRunRepository = workflowRunRepository;
        this.taskRunEngine = taskRunEngine;
        this.stateRunEngine = stateRunEngine;
    }

//    State machine follows a same strategy to even update and start as well
//    it right now checks the whole workflow and the whole state runs to see for the next state (
//    TODO : don't use this for advancing workflow BUILD A dependency graph and reverse index that to efficiently traverse the event (in future)
    public WorkflowRun advanceWorkflow(UUID workflowRunId,boolean justStarted){
      log.info("Workflow run advancement started for {}", workflowRunId);
      WorkflowRun workflowRun = workflowRunRepository.findById(workflowRunId).orElse(null);
      if(workflowRun == null){
          log.error("Workflow run advancement failed for {} (workflow not found[BUG])", workflowRunId);
          return null;
      }
      if(justStarted)
            taskRunEngine.setStart(workflowRun);
//      started at
      workflowRun.setStartedAt(Instant.now());
        EvaluationContext evaluationContext = new EvaluationContext(workflowRun,workflowRun.getWorkflowDefinition());

//        even when starting. if a user wants multiple tasks to be operating. then we need to run this (even when we are having a start state)
//        this method automatically publishes the required events.
        workflowRun.getTaskRuns().stream()
                .flatMap(taskRun -> taskRun.getStateRuns().stream())
                .forEach(stateRunEngine::updateInput);

        workflowRun.getTaskRuns().forEach(taskRun -> {taskRunEngine.update(taskRun,workflowRun,evaluationContext);});

//        setting the start state.


        try{
            workflowRunRepository.save(workflowRun);
        } catch (Exception e) {
            log.error("Workflow save failed at evaluation for  {}: {}", workflowRunId,workflowRun.getWorkflowDefinition().getName());
            throw new RuntimeException(e);
        }
        return workflowRun;
    }

}
