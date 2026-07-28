package com.Orka.repository;

import com.Orka.entities.runtime.WorkflowRun;
import io.grpc.stub.StreamObserver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface WorkflowRunRepository extends JpaRepository<WorkflowRun, UUID> {
    @Query("""
SELECT DISTINCT wr
FROM WorkflowRun wr
JOIN wr.workflowDefinition wd
LEFT JOIN wr.authorizations runAuth
LEFT JOIN wd.authorizationList defAuth
WHERE runAuth.username = :username
   OR defAuth.username = :username
""")
    List<WorkflowRun>findAuthorizedWorkflowRuns(@Param("username")String username);
}
