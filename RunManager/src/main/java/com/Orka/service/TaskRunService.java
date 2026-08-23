package com.Orka.service;

import com.Orka.apiContract.generated.*;
import com.Orka.entities.runtime.StateRun;
import com.Orka.entities.runtime.TaskRun;
import com.Orka.repository.StateRunRepository;
import com.Orka.repository.TaskRunRepository;
import com.Orka.service.publisher.TaskRunEventPublisher;
import com.Orka.util.JsonUtility;
import com.Orka.util.ProtoEnumMapper;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.shaded.com.google.protobuf.GeneratedMessageV3;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class TaskRunService {

    private final TaskRunRepository taskRunRepository;
    private final StateRunRepository stateRunRepository;
    private final TaskRunEventPublisher taskRunEventPublisher;
//    make this an interface ... if it is possible we might most prolly go for auth manager instead of a module in coming days .
    private final AuthAnsweringService authAnsweringService;

    public TaskRunService(TaskRunRepository taskRunRepository, StateRunRepository stateRunRepository, TaskRunEventPublisher taskRunEventPublisher, AuthAnsweringService authAnsweringService) {
        this.taskRunRepository = taskRunRepository;
        this.stateRunRepository = stateRunRepository;
        this.taskRunEventPublisher = taskRunEventPublisher;
        this.authAnsweringService = authAnsweringService;
    }


    public ProvideInputResponse _provideInput(ProvideInputRequest request){
//        TODO : once correctly configured re introduce the statement .
//        boolean canProvideInput = authAnsweringService.canUserProvideInput(request.getUsername(),UUID.fromString(request.getTaskRunId()));
        if(false)
            return getNotAvailableResponse("you are not authorized enough to provide input",403);
        UUID taskRunID = UUID.fromString(request.getTaskRunId());
        TaskRun taskRun = taskRunRepository.findById(taskRunID).orElse(null);
        if(taskRun==null){
            return getNotAvailableResponse("taskRun not found for the given id",404);
        }
        StateRun stateRun = taskRun.getCurrentStateRun();
        if(stateRun==null)
            return getNotAvailableResponse("no active state found for the given task run",404);
        if(stateRun.getStateDefinition().getInputDefinition()==null)
            return getNotAvailableResponse("the state does not accept any input please check",404);
        JsonNode givenInput = JsonUtility.translateFromProtobufValue(request.getInputVal());
        JsonNode currentInput = stateRun.getInput();
        currentInput = JsonUtility.setValue(currentInput,request.getPath(),givenInput);

        stateRun.setInput(currentInput);
        stateRunRepository.save(stateRun);

        return ProvideInputResponse.newBuilder().setDone(true)
                .setHttpResponse(
                        ProtoHttpResponse.newBuilder()
                                .setError("")
                                .setStatusCode(200).build()
                ).build();
    }


    public ProvideInputResponse provideInput(ProvideInputRequest request) {
        try {
            ProvideInputResponse response = _provideInput(request);

            byte[] event = TaskRunInputProvidedEvent.newBuilder()
                    .setTaskRunID(request.getTaskRunId())
                    .build()
                    .toByteArray();

            taskRunEventPublisher.publish_input_provided_event(
                    UUID.fromString(request.getTaskRunId()),
                    event
            );

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private ProvideInputResponse getNotAvailableResponse(String message,int statusCode){
        return ProvideInputResponse.newBuilder().
                setDone(false).
                setHttpResponse(
                        ProtoHttpResponse.newBuilder()
                                .setError(message)
                                .setStatusCode(statusCode)
                                .build()
                ).build();
    }


//    BULK REQUEST
    public GetAllTaskRunsResponse getAllTaskRuns(String username){
        List<TaskRun>authorizedTaskRuns = taskRunRepository.findAll();
        List<TaskRunDTO>taskRunDTOS = authorizedTaskRuns.stream()
                .map(this::toTaskRunDTO)
                .toList();
        return GetAllTaskRunsResponse.newBuilder().addAllTaskRuns(taskRunDTOS).build();
    }
    private TaskRunDTO toTaskRunDTO(TaskRun taskRun) {

        List<TaskRunAuthorization> taskRunAuthorizationDTOS =
                taskRun.getAuthorizations()
                        .stream()
                        .map(this::toTaskRunAuthDTO)
                        .toList();

        TaskRunDTO.Builder builder = TaskRunDTO.newBuilder();

        if (taskRun.getId() != null) {
            builder.setId(taskRun.getId().toString());
        }

        if(taskRun.getWorkflowRun().getWorkflowDefinition()!=null){
            log.info("[we are sending owner] : {} ",taskRun.getWorkflowRun().getWorkflowDefinition().getCreatorName());
            log.info("[we] are sending defnition name {} ",taskRun.getWorkflowRun().getWorkflowDefinition().getName());
            builder.setWorkflowDefinitionName(taskRun.getWorkflowRun().getWorkflowDefinition().getName());
            builder.setOwner(taskRun.getWorkflowRun().getWorkflowDefinition().getCreatorName());
        }



        if (taskRun.getWorkflowRun() != null && taskRun.getWorkflowRun().getId() != null) {
            builder.setWorkflowRunId(taskRun.getWorkflowRun().getId().toString());
        }

        if (taskRun.getTaskDefinition() != null && taskRun.getTaskDefinition().getId() != null) {
            builder.setTaskDefinitionId(taskRun.getTaskDefinition().getId().toString());
        }

        if (taskRun.getTaskDefinitionName() != null) {
            builder.setTaskDefinitionName(taskRun.getTaskDefinitionName());
        }

        if (taskRun.getCurrentStateRun() != null
                && taskRun.getCurrentStateRun().getStateDefinition() != null
                && taskRun.getCurrentStateRun().getStateDefinition().getId() != null) {
            builder.setCurrentStateDefinitionId(
                    taskRun.getCurrentStateRun()
                            .getStateDefinition()
                            .getId()
                            .toString());
            log.info("[we are sending the current internal state as] {} for task run {}",taskRun.getCurrentStateRun().getStateDefinition().getInternalState(), taskRun.getCurrentStateRun().getStateDefinition().getId());
            builder.setCurrentInternalState(ProtoEnumMapper.toProto(taskRun.getCurrentStateRun().getStateDefinition().getInternalState(),OrkaInternalState.class));
        }

        if (taskRun.getCurrentStateDefinitionName() != null) {
            builder.setCurrentStateDefinitionName(
                    taskRun.getCurrentStateDefinitionName());
        }

        if (taskRun.getCurrentStateRun() != null
                && taskRun.getCurrentStateRun().getId() != null) {
            builder.setCurrentStateRunId(
                    taskRun.getCurrentStateRun().getId().toString());
        }

        builder.setRetryCount(taskRun.getRetryCount());

        if (taskRun.getStatus() != null) {
            builder.setStatus(taskRun.getStatus().toString());
        }

        if (taskRun.getStartedAt() != null) {
            builder.setStartedAt(taskRun.getStartedAt().toString());
        }

        if (taskRun.getCompletedAt() != null) {
            builder.setCompletedAt(taskRun.getCompletedAt().toString());
        }

        builder.addAllAuthorizations(taskRunAuthorizationDTOS);

        return builder.build();
    }
    private TaskRunAuthorization toTaskRunAuthDTO(com.Orka.entities.authorization.TaskRunAuthorization taskRunAuthorization){
        return TaskRunAuthorization.newBuilder()
                .setUsername(taskRunAuthorization.getUsername())
                .setAuthRole(ProtoEnumMapper.toProto(taskRunAuthorization.getAuthRole(),TaskRunAuthRole.class))
                .build();
    }

    public GetSingleTaskRunResponse getTaskRunById(String id,String username){
        UUID taskRunId =  UUID.fromString(id);
        TaskRun taskRun = taskRunRepository.findById(taskRunId).orElse(null);
        if(taskRun==null){
            return getResponse(404,"task run by id not found",false);
        }
//        TODO : find why this is not worlking and remove the workaround

//        boolean isAuthorized = authAnsweringService.hasTaskRunAccess(taskRun,taskRunId,username);
        boolean isAuthorized = true;
        if(!isAuthorized){
            return getResponse(403,"not enough permissions to get this task",false);
        }
//        got the task and the user is authorized if the code comes past those two checks
        TaskRunDTO taskRunDTO = toTaskRunDTO(taskRun);

        GetSingleTaskRunResponse.Builder builder =
                GetSingleTaskRunResponse.newBuilder();

        if (taskRunDTO != null) {
            builder.setTaskRun(taskRunDTO);
        }

        if (taskRun.getCurrentStateRun() != null) {
            builder.setCurrentStateRun(
                    toStateRunDTO(taskRun.getCurrentStateRun()));

        }

        builder.setHttpResponse(
                ProtoHttpResponse.newBuilder()
                        .setStatusCode(200)
                        .setError("")
                        .build()
        );

        return builder.build();
    }


    private StateRunDTO toStateRunDTO(StateRun stateRun){
        var builder = StateRunDTO.newBuilder().setStateRunId(stateRun.getId().toString())
                .setInputSchema(stateRun.getStateDefinition().getInputDefinition().getJsonSchema())
                .setOutputSchema(stateRun.getStateDefinition().getOutputDefinition().getJsonSchema())
                .setInputValue(JsonUtility.translateToProtobufValue(stateRun.getInput()))
                .setOutputValue(JsonUtility.translateToProtobufValue(stateRun.getOutput()))
                .setInternalState(ProtoEnumMapper.toProto(stateRun.getStateDefinition().getInternalState(),OrkaInternalState.class));
        if(stateRun.getStateDefinition().getScriptDefinition()!=null){
            builder.setScriptDefinition(toScriptDefinitionDTO(stateRun.getStateDefinition().getScriptDefinition()));
        }

        return builder.build();
    }

    private GetSingleTaskRunResponse getResponse(int statusCode,String message,boolean isSuccess){
        return GetSingleTaskRunResponse.newBuilder().setHttpResponse(
                ProtoHttpResponse.newBuilder().setError(message)
                        .setStatusCode(statusCode)
                        .build()
        ).build();
    }

    private ScriptDefinition toScriptDefinitionDTO(com.Orka.entities.definition.ScriptDefinition scriptDefinition){
        return ScriptDefinition.newBuilder().
                setDockerImage(scriptDefinition.getDockerImage())
                .setScriptName(scriptDefinition.getScriptName())
                .setEntryCommand(scriptDefinition.getEntryCommand())
                .build();
    }
}
