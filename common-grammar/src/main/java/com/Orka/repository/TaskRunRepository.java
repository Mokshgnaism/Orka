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
    select DISTINCT tr from TaskRun tr
    JOIN tr.taskDefinition td\s
    JOIN tr.workflowRun wr\s
    JOIN wr.workflowDefinition wd\s
    LEFT JOIN tr.authorizations auth
    LEFT JOIN td.authorizations tdAuth
    LEFT JOIN wr.authorizations wfAuth
    LEFT JOIN wd.authorizationList wdAuth
    where auth.username = :username
    OR tdAuth.username = :username
    OR wfAuth.username = :username
    OR wdAuth.username = :username
""")
    List<TaskRun>findAuthorizedTaskRuns(@Param("username")String username);
}
