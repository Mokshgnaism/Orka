package com.Orka.service;

import com.Orka.apiContract.generated.StateRunStartedEvent;
import com.Orka.docker.DockerScriptExecutor;
import com.Orka.entities.runtime.ScriptExecution;
import com.Orka.entities.runtime.StateRun;
import com.Orka.repository.ScriptExecutionRepository;
import com.Orka.repository.StateRunRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Slf4j
@Transactional
@Service
public class ScriptRunner {
    private final StateRunRepository stateRunRepository;
    private final ScriptExecutionRepository scriptExecutionRepository;
    private  final DockerScriptExecutor scriptDockerScriptExecutor;
    private final ExecutorService executorService;
    public ScriptRunner(StateRunRepository stateRunRepository, ScriptExecutionRepository scriptExecutionRepository, DockerScriptExecutor dockerScriptExecutor, ExecutorService executorService) {
        this.stateRunRepository = stateRunRepository;
        this.scriptExecutionRepository = scriptExecutionRepository;
        this.scriptDockerScriptExecutor = dockerScriptExecutor;
        this.executorService = executorService;
        System.out.println("====== ScriptRunner constructor ======");
    }

    public  void detectAndStartScript(StateRunStartedEvent stateRunStartedEvent) {
        log.info("[HUMAN]ScriptRunner instance = {}", System.identityHashCode(this));
        log.info("[HUMAN]stateRunRepository = {}", stateRunRepository);
        log.info("[HUMAN]scriptExecutionRepository = {}", scriptExecutionRepository);
        log.info("[human]executorService = {}", executorService);
        Optional<StateRun> stateRunOptional = stateRunRepository.findById(UUID.fromString(stateRunStartedEvent.getId()));
        if(stateRunOptional.isEmpty()) {
            log.error("State Run Not Found for event {}", stateRunStartedEvent);
            return;
        }
        StateRun stateRun = stateRunOptional.get();
        log.info("State Run Started Event Received: {}", stateRun);
        if(stateRun.getStateDefinition().getScriptDefinition()==null){
            log.info("script definition not present [human task]");
            return;
        }
        log.info("script definition present [automated task]");
        Optional<ScriptExecution> presentScriptExecution = scriptExecutionRepository.findByStateRun_Id(stateRun.getId());
        ScriptExecution scriptExecution;
        scriptExecution = presentScriptExecution.orElseGet(ScriptExecution::new);
//        idempotent
        scriptExecution.setScriptDefinition(stateRun.getStateDefinition().getScriptDefinition());
        scriptExecution.setStateRun(stateRun);
//        idempotent
        if(scriptExecution.getAttemptNumber()==null){
            scriptExecution.setAttemptNumber(1);
        }else{
            scriptExecution.setAttemptNumber(scriptExecution.getAttemptNumber()+1);
        }
        if(scriptExecution.getCompletedAt()!=null){
//            TODO : use a notification to notify user
            log.warn("task stuck in same state [notify user]");
            return;
        }
        if(scriptExecution.isRunning()){
            log.info("script execution already running");
            return;
        }
//        TODO : DEADLOCK CHANCE reduced but not ZERO use proper lock system .
        scriptExecution.setRunning(true);

        scriptExecutionRepository.save(scriptExecution);

        if(scriptExecution.getStartedAt()==null){
            scriptExecution.setStartedAt(Instant.now());
        }
        executorService.submit(
                ()-> scriptDockerScriptExecutor.executeScript(scriptExecution)
        );
        return ;
    }
}
