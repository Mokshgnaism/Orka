package com.Orka.entities.authorization;

import com.Orka.ENUM.AuthEnums.TASK_RUN_AUTH_ROLE;
import com.Orka.entities.runtime.TaskRun;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TaskRunAuthorization implements Authorization {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private TaskRun taskRun;
    private String username;
    private TASK_RUN_AUTH_ROLE authRole;
}
