package com.Orka.service;

import com.Orka.apiContract.generated.ProtoHttpResponse;
import com.Orka.apiContract.generated.ProvideInputRequest;
import com.Orka.apiContract.generated.ProvideInputResponse;
import com.Orka.apiContract.generated.TaskRunInputProvidedEvent;
import com.Orka.entities.runtime.StateRun;
import com.Orka.entities.runtime.TaskRun;
import com.Orka.repository.StateRunRepository;
import com.Orka.repository.TaskRunRepository;
import com.Orka.service.publisher.TaskRunEventPublisher;
import com.Orka.util.JsonUtility;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.transaction.Transactional;
import java.util.UUID;

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

    @Transactional
    public ProvideInputResponse _provideInput(ProvideInputRequest request){
        boolean canProvideInput = authAnsweringService.canUserProvideInput(request.getUsername(),UUID.fromString(request.getTaskRunId()));
        if(!canProvideInput)
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


    public ProvideInputResponse provideInput(ProvideInputRequest request){
        ProvideInputResponse response = _provideInput(request);
        byte[] event = TaskRunInputProvidedEvent.newBuilder().setTaskRunID(request.getTaskRunId()).build().toByteArray();
        taskRunEventPublisher.publish_input_provided_event(UUID.fromString(request.getTaskRunId()),event);
        return response;
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
}
