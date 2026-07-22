package com.Orka.entities.definition;
import com.Orka.internal.VariableDefinition;
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
public class ScriptDefinition {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @OneToOne
    private StateDefinition stateDefinition;

    String scriptName;

    Integer version;

    String dockerImage;

    String entryCommand;

    Integer timeout ; // in milliseconds

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "script_environment_variables",
            joinColumns = @JoinColumn(name = "script_id"),
            inverseJoinColumns = @JoinColumn(name = "variable_id")
    )
    List<VariableDefinition> environmentVariables;
}
