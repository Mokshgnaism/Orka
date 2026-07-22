package com.Orka.Assembler;
import com.Orka.entities.runtime.WorkflowRun;
import com.Orka.internal.Variable;
import com.Orka.internal.VariableDefinition;

public class VariableAssembler {
    public static  Variable  assemble(VariableDefinition variableDefinition, WorkflowRun workflowRun){
        Variable variable = new Variable();
        variable.setName(variableDefinition.getName());

        variable.setVariableDefinition(variableDefinition);
        variable.setWorkflowRun(workflowRun);
        if(variableDefinition.getDefaultValue()!=null){
            variable.setValue(variableDefinition.getDefaultValue());
        }
        else{
            variable.setValue(null);
        }
        return variable;
    }
}
