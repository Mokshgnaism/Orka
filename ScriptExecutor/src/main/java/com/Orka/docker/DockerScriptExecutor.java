package com.Orka.docker;

import com.Orka.apiContract.generated.ScriptExecutionResult;
import com.Orka.constants.ExecutionContract;
import com.Orka.entities.definition.ScriptDefinition;
import com.Orka.entities.runtime.ScriptExecution;
import com.Orka.entities.runtime.StateRun;
import com.Orka.publisher.KafkaPublisher;
import com.Orka.repository.ScriptExecutionRepository;
import com.Orka.repository.StateRunRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class DockerScriptExecutor {
    private final KafkaPublisher kafkaPublisher;
    private final ExecutorService executorService;
    private final ScriptExecutionRepository scriptExecutionRepository;
    private final StateRunRepository stateRunRepository;
//    for more clean design eliminate this repo and send output on the kafka even and let teh automata decide what to do with output .

    public DockerScriptExecutor(KafkaPublisher kafkaPublisher, ExecutorService executorService, ScriptExecutionRepository scriptExecutionRepository, StateRunRepository stateRunRepository) {
        this.kafkaPublisher = kafkaPublisher;
        this.executorService = executorService;
        this.scriptExecutionRepository = scriptExecutionRepository;
        this.stateRunRepository = stateRunRepository;
    }

    public void executeScript(ScriptExecution scriptExecution) {
        ScriptDefinition scriptDefinition = scriptExecution.getScriptDefinition();
        String userEntryCommand = scriptDefinition.getEntryCommand();
        String dockerImage = scriptDefinition.getDockerImage();
        JsonNode input = scriptExecution.getStateRun().getInput();
        Path workspace = null;
        Path outputFile = null;
        Path inputFile = null;
        try {
            workspace = Files.createTempDirectory("orka-"+scriptExecution.getId());
            inputFile = workspace.resolve("input.json");
            outputFile = workspace.resolve("output.json");
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(inputFile.toFile(), input);
            ProcessBuilder pb = new ProcessBuilder(
                    "docker",
                    "run",
                    "--rm",
                    "-v",
                    workspace.toAbsolutePath() + ":" + ExecutionContract.CONTAINER_ROOT,
                    dockerImage,
                    "sh",
                    "-c",
                    userEntryCommand
            );
            Process process = pb.start();
            boolean finished = process.waitFor(ExecutionContract.MAX_TIME, TimeUnit.SECONDS);

            CompletableFuture<StringBuilder> cfStdOut = CompletableFuture.supplyAsync(
                    ()->{
                        StringBuilder stdOut = new StringBuilder();
                        try(BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));){
                            String line;
                            while ((line = reader.readLine()) != null) {
                                stdOut.append(line).append(System.lineSeparator());
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        return stdOut;
                    },
                    executorService
            );
            CompletableFuture<StringBuilder> cfStdErr = CompletableFuture.supplyAsync(
                    ()->{
                        StringBuilder stdErr = new StringBuilder();
                        try(BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));){
                            String line;
                            while ((line = reader.readLine()) != null) {
                                stdErr.append(line).append(System.lineSeparator());
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        return stdErr;
                    },
                    executorService
            );
            if(!finished){
                process.destroyForcibly();
                log.error("Docker did not finish after " + ExecutionContract.MAX_TIME + " seconds. for {}", scriptExecution.getId());
                ScriptExecutionResult scriptExecutionResult = ScriptExecutionResult.newBuilder().setError("Docker did not finish after " + ExecutionContract.MAX_TIME + " seconds. "+cfStdErr.get()+cfStdOut.get()).build();
                kafkaPublisher.publish_scriptExecution(scriptExecutionResult);
                return;
            }

            CompletableFuture<Void>cfFanIn = CompletableFuture.allOf(cfStdOut, cfStdErr);
            cfFanIn.get();

            String stdOut = cfStdOut.get().toString();
            String stdErr = cfStdErr.get().toString();
            JsonNode output = null;
            if (outputFile != null && Files.exists(outputFile)) {
                output = objectMapper.readTree(outputFile.toFile());
                log.info("[HUMAN] : script output  : {}",output);
            } else {
                log.error("Output file was not created: {}", outputFile);
            }
            log.info("[HUMAN] : script std out : {}",stdOut);
            log.info("[HUMAN] : script std err : {}",stdErr);


            scriptExecution.setRunning(false);
            scriptExecution.setStdout(stdOut);
            scriptExecution.setStderr(stdErr);
            scriptExecution.setExitCode(process.exitValue());
//            TODO : write to output of the state run first . (at automata service)
            StateRun stateRun  = scriptExecution.getStateRun();
            stateRun.setOutput(output);

            stateRunRepository.save(stateRun);

            scriptExecutionRepository.save(scriptExecution);
            ScriptExecutionResult scriptExecutionResult = ScriptExecutionResult.newBuilder().
                    setExitCode(scriptExecution.getExitCode())
                    .setError("")
                    .setStderr(stdErr).
                    setStateRunId(stateRun.getId().toString()).
                    setStdout(stdOut).build();
            kafkaPublisher.publish_scriptExecution(scriptExecutionResult);

            log.info("Workspace Path: {}", workspace);
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("error creating workspace path");
//             we need to publish an event for failed script execution with number for notification manager so it can notify users about failure
            throw new RuntimeException(e);
        }
    }

}
