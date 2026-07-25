package com.Orka.Store;
import com.Orka.entities.authorization.TaskDefinitionAuthorization;
import com.Orka.entities.authorization.TaskRunAuthorization;
import com.Orka.entities.authorization.WorkflowDefinitionAuthorization;
import com.Orka.entities.authorization.WorkflowRunAuthorization;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class Store {

    private Store() {}

    public static final ReentrantReadWriteLock LOCK =
            new ReentrantReadWriteLock();

    public static final ConcurrentHashMap<String, Set<TaskDefinitionAuthorization>>
            taskDefinitionAuthorizations = new ConcurrentHashMap<>();

    public static final ConcurrentHashMap<String, Set<WorkflowDefinitionAuthorization>>
            workflowDefinitionAuthorizations = new ConcurrentHashMap<>();

    public static final ConcurrentHashMap<String, Set<TaskRunAuthorization>>
            taskRunAuthorizations = new ConcurrentHashMap<>();

    public static final ConcurrentHashMap<String, Set<WorkflowRunAuthorization>>
            workflowRunAuthorizations = new ConcurrentHashMap<>();
}