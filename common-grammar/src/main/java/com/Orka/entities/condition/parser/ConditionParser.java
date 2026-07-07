package com.Orka.entities.condition.parser;

import com.Orka.entities.condition.Condition;

public interface ConditionParser {
    Condition parse(String expression);
}
