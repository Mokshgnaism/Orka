package com.Orka.entities.condition;

import com.Orka.entities.definition.WorkflowDefinition;
import com.Orka.interfaces.Repository;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "condition_type")
@ToString
public abstract class AtomicCondition {
    //    public UUID workflowDefinitionId;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @Column(name = "workflow_definition_id")
    private UUID workflowDefinitionId;

    @ManyToOne
    @JoinColumn(name = "condition_id")
    private Condition condition;

//    do we really need the condition to be get from the workflow definition i donot think so . but lets leave this and keep the workflowDefinitionID . for the assembler compatibility and lets have this removed right now .
//    private WorkflowDefinition workflowDefinition;
    public abstract boolean evaluate(EvaluationContext context, Repository repo);

}