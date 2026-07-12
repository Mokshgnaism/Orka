package com.Orka.entities.condition;

public enum ComparisonOperator {

    EQUAL,

    NOT_EQUAL,

    GREATER_THAN,

    GREATER_THAN_EQUAL,

    LESS_THAN,

    LESS_THAN_EQUAL,

    EXISTS;

    public static ComparisonOperator fromProto(
            com.Orka.apiContract.generated.ComparisonOperator proto) {

        return ComparisonOperator.valueOf(proto.name());
    }

    public com.Orka.apiContract.generated.ComparisonOperator toProto() {

        return com.Orka.apiContract.generated.ComparisonOperator.valueOf(name());

    }
}