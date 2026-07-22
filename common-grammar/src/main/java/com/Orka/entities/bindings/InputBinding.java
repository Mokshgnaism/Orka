package com.Orka.entities.bindings;
import com.Orka.entities.datareference.DataReference;
import com.Orka.entities.definition.InputDefinition;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class InputBinding {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * JSONPath inside the Input JSON.
     *
     * Example:
     *
     * $.github.repository
     */
    private String destinationJsonPath;

    @ManyToOne
    private InputDefinition inputDefinition;
    /**
     * Where should the value come from?
     */
    @OneToOne(cascade = CascadeType.ALL)
    private DataReference source;

}
