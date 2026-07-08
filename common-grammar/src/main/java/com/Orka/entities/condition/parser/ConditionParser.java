package com.Orka.entities.condition.parser;

import com.Orka.entities.condition.Condition;
@Deprecated
public interface ConditionParser {
    Condition parse(String expression);
}
