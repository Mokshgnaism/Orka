package com.Orka.service;

import com.Orka.entities.bindings.InputBinding;
import com.Orka.entities.condition.EvaluationContext;
import com.Orka.entities.runtime.StateRun;
import com.Orka.entities.runtime.WorkflowRun;
import com.Orka.util.JsonUtility;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class StateRunEngine {
    public void updateInput(StateRun stateRun){
        JsonNode input = stateRun.getInput();
        WorkflowRun workflowRun = stateRun.getTaskRun().getWorkflowRun();
        EvaluationContext evaluationContext = new EvaluationContext(workflowRun,workflowRun.getWorkflowDefinition());
        if(stateRun.getStateDefinition().getInputDefinition().getBindings()==null){
            stateRun.getStateDefinition().getInputDefinition().setBindings(new ArrayList<>());
        }
        List<InputBinding>inputBindings = stateRun.getStateDefinition().getInputDefinition().getBindings();
        for (InputBinding inputBinding : inputBindings) {
            String destinationPath = inputBinding.getDestinationJsonPath();
            JsonNode value = inputBinding.getSource().getValue(evaluationContext);
            input = JsonUtility.setValue(input,destinationPath,value);
        }
        stateRun.setInput(input);
    }
}
