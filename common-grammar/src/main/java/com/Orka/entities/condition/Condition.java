package com.Orka.entities.condition;
import lombok.Builder;
import lombok.Getter;
import java.util.List;
@Getter
@Builder
public class Condition {
    private List<AtomicCondition> atomicConditions;
    private String expression;
//    we are naming this and defaults to stateNameCondition
    private String name;

}