package com.Orka.repository;

import com.Orka.entities.runtime.TaskRun;
import com.Orka.entities.runtime.WorkflowRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface TaskRunRepository extends JpaRepository<TaskRun, UUID> {
    TaskRun findByWorkflowRunAndTaskDefinition_Id(WorkflowRun workflowRun, UUID taskDefinitionId);

    @Query("""
    select tr from TaskRun tr
    JOIN tr.authorizations auth
    where auth.username = :username
""")
    List<TaskRun>findAuthorizedTaskRuns(@Param("username")String username);
}
