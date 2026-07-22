package com.Orka.entities.definition;

import com.Orka.entities.bindings.InputBinding;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class InputDefinition {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * JSON Schema
     */
    private String jsonSchema;

    /**
     * How every field inside the schema is populated.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true,mappedBy = "inputDefinition")
    private List<InputBinding> bindings;

}