package com.Orka.entities.bindings;
import com.Orka.entities.datareference.DataReference;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;
@Builder
@Getter
public class InputBinding {

    private UUID id;

    /**
     * JSONPath inside the Input JSON.
     *
     * Example:
     *
     * $.github.repository
     */
    private String destinationJsonPath;

    /**
     * Where should the value come from?
     */
    private DataReference source;

}
