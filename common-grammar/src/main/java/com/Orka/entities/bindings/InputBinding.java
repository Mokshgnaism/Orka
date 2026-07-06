package com.Orka.entities.bindings;
import com.Orka.entities.datareference.DataReference;

import java.util.UUID;
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
