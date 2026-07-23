package com.Orka.internalStates;

import com.Orka.ENUM.typeEnums.ORKA_INTERNAL_STATE;
import com.Orka.entities.definition.StateDefinition;
import com.Orka.entities.definition.TaskDefinition;

public class ConstantInternalStates {
    public static StateDefinition getInternalStateDefinition() {
        StateDefinition stateDefinition = new StateDefinition();
        stateDefinition.setName("FAILED");
        stateDefinition.setInternalState(ORKA_INTERNAL_STATE.ABORTED);
        return stateDefinition;
    };
}
