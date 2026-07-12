package com.Orka.util;

public final class ProtoEnumMapper {

    public static <T extends Enum<T>> T toEntity(
            Enum<?> protoEnum,
            Class<T> entityEnum) {

        if (protoEnum == null)
            return null;

        return Enum.valueOf(entityEnum, protoEnum.name());
    }

    public static <T extends Enum<T>> T toProto(
            Enum<?> entityEnum,
            Class<T> protoEnum) {

        if (entityEnum == null)
            return null;

        return Enum.valueOf(protoEnum, entityEnum.name());
    }
}