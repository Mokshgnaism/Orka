------------------------------------------------------------
-- ORKA CREATE.SQL
-- PART 1
-- Definition Layer
------------------------------------------------------------

------------------------------------------------------------
-- ENUMS
------------------------------------------------------------

CREATE TYPE orka_internal_state AS ENUM
    (
    'WAITING_FOR_INPUT',
    'RUNNING',
    'COMPLETED',
    'ABORTED'
);

CREATE TYPE variable_type AS ENUM
    (
    'STRING',
    'INTEGER',
    'ENUM'
);

ALTER TYPE variable_type
add value 'INTEGER';

ALTER TYPE variable_type
    add value 'BOOLEAN';
ALTER TYPE variable_type
    add value 'JSON';
ALTER TYPE variable_type
    add value 'STRING';

CREATE TYPE data_reference_type AS ENUM
    (
    'STATE_INPUT',
    'STATE_OUTPUT',
    'WORKFLOW_VARIABLE',
    'CONSTANT'
);

CREATE TYPE comparison_operator AS ENUM
    (
    'EQUAL',
    'NOT_EQUAL',

    'LESS_THAN',
    'LESS_THAN_EQUAL',

    'GREATER_THAN',
    'GREATER_THAN_EQUAL'
);

CREATE TYPE atomic_condition_type AS ENUM
    (
    'STATE_IN',
    'STATE_INPUT',
    'STATE_OUTPUT',
    'WORKFLOW_VARIABLE'
);
------------------------------------------------------------
-- AUTHORIZATION ENUMS
------------------------------------------------------------

CREATE TYPE workflow_definition_auth_role AS ENUM
    (
        'MANAGER',
        'CONFIGURATOR',
        'VIEWER'
        );

CREATE TYPE workflow_run_auth_role AS ENUM
    (
        'MANAGER',
        'CONFIGURATOR'
        );

CREATE TYPE task_definition_auth_role AS ENUM
    (
        'MANAGER',
        'CONFIGURATOR',
        'VIEWER'
        );

CREATE TYPE task_run_auth_role AS ENUM
    (
        'ASSIGNEE',
        'VIEWER'
        );
------------------------------------------------------------
-- WORKFLOW DEFINITIONS
------------------------------------------------------------

CREATE TABLE workflow_definition
(
    id UUID PRIMARY KEY,

    name VARCHAR(200) NOT NULL,

    description TEXT,

    version INTEGER NOT NULL DEFAULT 1,

    created_by VARCHAR(200),

    start_task_definition_id UUID,

    start_state_definition_id UUID
);

ALTER TABLE workflow_definition
add column created_at DATE;

------------------------------------------------------------
-- VARIABLE DEFINITIONS
------------------------------------------------------------

CREATE TABLE variable_definition
(
    id UUID PRIMARY KEY,

    workflow_definition_id UUID NOT NULL,

    name VARCHAR(150) NOT NULL,

    variable_type variable_type NOT NULL,

    default_value JSONB,

    CONSTRAINT fk_variable_definition_workflow
        FOREIGN KEY(workflow_definition_id)
            REFERENCES workflow_definition(id)
            ON DELETE CASCADE,

    CONSTRAINT uq_variable_definition_name
        UNIQUE(workflow_definition_id,name)
);

------------------------------------------------------------
-- TASK DEFINITIONS
------------------------------------------------------------

CREATE TABLE task_definition
(
    id UUID PRIMARY KEY,

    workflow_definition_id UUID NOT NULL,

    name VARCHAR(200) NOT NULL,

    description TEXT,

    CONSTRAINT fk_task_definition_workflow
        FOREIGN KEY(workflow_definition_id)
            REFERENCES workflow_definition(id)
            ON DELETE CASCADE,

    CONSTRAINT uq_task_definition_name
        UNIQUE(workflow_definition_id,name)
);


------------------------------------------------------------
-- WORKFLOW DEFINITION AUTHORIZATION
------------------------------------------------------------

CREATE TABLE workflow_definition_authorization
(
    id UUID PRIMARY KEY,

    workflow_definition_id UUID NOT NULL,

    principal_name VARCHAR(250) NOT NULL,

    role workflow_definition_auth_role NOT NULL,

    CONSTRAINT fk_workflow_definition_authorization
        FOREIGN KEY (workflow_definition_id)
            REFERENCES workflow_definition(id)
            ON DELETE CASCADE,

    CONSTRAINT uq_workflow_definition_authorization
        UNIQUE(workflow_definition_id, principal_name)
);

------------------------------------------------------------
-- TASK DEFINITION AUTHORIZATION
------------------------------------------------------------

CREATE TABLE task_definition_authorization
(
    id UUID PRIMARY KEY,

    task_definition_id UUID NOT NULL,

    principal_name VARCHAR(250) NOT NULL,

    role task_definition_auth_role NOT NULL,

    CONSTRAINT fk_task_definition_authorization
        FOREIGN KEY (task_definition_id)
            REFERENCES task_definition(id)
            ON DELETE CASCADE,

    CONSTRAINT uq_task_definition_authorization
        UNIQUE(task_definition_id, principal_name)
);

------------------------------------------------------------
-- WORKFLOW RUN AUTHORIZATION
------------------------------------------------------------

CREATE TABLE workflow_run_authorization
(
    id UUID PRIMARY KEY,

    workflow_run_id UUID NOT NULL,

    principal_name VARCHAR(250) NOT NULL,

    role workflow_run_auth_role NOT NULL,

    CONSTRAINT fk_workflow_run_authorization
        FOREIGN KEY (workflow_run_id)
            REFERENCES workflow_run(id)
            ON DELETE CASCADE,

    CONSTRAINT uq_workflow_run_authorization
        UNIQUE(workflow_run_id, principal_name)
);

------------------------------------------------------------
-- TASK RUN AUTHORIZATION
------------------------------------------------------------

CREATE TABLE task_run_authorization
(
    id UUID PRIMARY KEY,

    task_run_id UUID NOT NULL,

    principal_name VARCHAR(250) NOT NULL,

    role task_run_auth_role NOT NULL,

    CONSTRAINT fk_task_run_authorization
        FOREIGN KEY (task_run_id)
            REFERENCES task_run(id)
            ON DELETE CASCADE,

    CONSTRAINT uq_task_run_authorization
        UNIQUE(task_run_id, principal_name)
);



------------------------------------------------------------
-- STATE DEFINITIONS
------------------------------------------------------------

CREATE TABLE state_definition
(
    id UUID PRIMARY KEY,

    task_definition_id UUID NOT NULL,

    name VARCHAR(200) NOT NULL,

    priority INTEGER NOT NULL,

    internal_state orka_internal_state NOT NULL,

    CONSTRAINT fk_state_definition_task
        FOREIGN KEY(task_definition_id)
            REFERENCES task_definition(id)
            ON DELETE CASCADE,

    CONSTRAINT uq_state_name
        UNIQUE(task_definition_id,name),

    CONSTRAINT uq_state_priority
        UNIQUE(task_definition_id,priority)
);

------------------------------------------------------------
-- INPUT DEFINITIONS
------------------------------------------------------------

CREATE TABLE input_definition
(
    id UUID PRIMARY KEY,

    state_definition_id UUID NOT NULL UNIQUE,

    json_schema JSONB NOT NULL,

    CONSTRAINT fk_input_definition_state
        FOREIGN KEY(state_definition_id)
            REFERENCES state_definition(id)
            ON DELETE CASCADE
);
ALTER TABLE output_definition
drop column json_schema ;

ALTER TABLE output_definition
add column json_schema TEXT;
------------------------------------------------------------
-- OUTPUT DEFINITIONS
------------------------------------------------------------

CREATE TABLE output_definition
(
    id UUID PRIMARY KEY,

    state_definition_id UUID NOT NULL UNIQUE,

    json_schema JSONB NOT NULL,

    CONSTRAINT fk_output_definition_state
        FOREIGN KEY(state_definition_id)
            REFERENCES state_definition(id)
            ON DELETE CASCADE
);

------------------------------------------------------------
-- SCRIPT DEFINITIONS
------------------------------------------------------------

CREATE TABLE script_definition
(
    id UUID PRIMARY KEY,

    state_definition_id UUID NOT NULL UNIQUE,

    script_name VARCHAR(200) NOT NULL,

    docker_image TEXT NOT NULL,

    command_template TEXT NOT NULL,

    timeout_ms INTEGER NOT NULL,

    CONSTRAINT fk_script_definition_state
        FOREIGN KEY(state_definition_id)
            REFERENCES state_definition(id)
            ON DELETE CASCADE
);

------------------------------------------------------------
-- SCRIPT ENVIRONMENT VARIABLES
------------------------------------------------------------

CREATE TABLE script_environment_variable
(
    id UUID PRIMARY KEY,

    script_definition_id UUID NOT NULL,

    variable_definition_id UUID NOT NULL,

    CONSTRAINT fk_env_variable_to_variable_definition
        FOREIGN KEY (variable_definition_id)
            REFERENCES variable_definition(id)
            ON DELETE CASCADE ,

    CONSTRAINT fk_script_environment_variable
        FOREIGN KEY(script_definition_id)
            REFERENCES script_definition(id)
            ON DELETE CASCADE,

    CONSTRAINT uq_script_environment_variable
        UNIQUE(script_definition_id,variable_definition_id)
);

------------------------------------------------------------
-- DATA REFERENCES
------------------------------------------------------------

CREATE TABLE data_reference
(
    id UUID PRIMARY KEY,

    type data_reference_type NOT NULL,

    task_definition_id UUID,

    state_definition_id UUID,

    json_path TEXT,

    variable_definition_id UUID,

    constant_value JSONB,

    CONSTRAINT fk_reference_task
        FOREIGN KEY(task_definition_id)
            REFERENCES task_definition(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_reference_state
        FOREIGN KEY(state_definition_id)
            REFERENCES state_definition(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_reference_variable
        FOREIGN KEY(variable_definition_id)
            REFERENCES variable_definition(id)
            ON DELETE CASCADE
);

------------------------------------------------------------
-- INPUT BINDINGS
------------------------------------------------------------

CREATE TABLE input_binding
(
    id UUID PRIMARY KEY,

    input_definition_id UUID NOT NULL,

    destination_json_path TEXT NOT NULL,

    data_reference_id UUID NOT NULL,

    CONSTRAINT fk_input_binding_definition
        FOREIGN KEY(input_definition_id)
            REFERENCES input_definition(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_input_binding_reference
        FOREIGN KEY(data_reference_id)
            REFERENCES data_reference(id)
            ON DELETE CASCADE
);

------------------------------------------------------------
-- CONDITIONS
------------------------------------------------------------

CREATE TABLE condition_definition
(
    id UUID PRIMARY KEY,

    workflow_definition_id UUID NOT NULL,

    name VARCHAR(200) NOT NULL,

    expression TEXT NOT NULL,

    CONSTRAINT fk_condition_workflow
        FOREIGN KEY(workflow_definition_id)
            REFERENCES workflow_definition(id)
            ON DELETE CASCADE,

    CONSTRAINT uq_condition_name
        UNIQUE(workflow_definition_id,name)
);

------------------------------------------------------------
-- ATOMIC CONDITIONS
------------------------------------------------------------

CREATE TABLE atomic_condition
(
    id UUID PRIMARY KEY,

    workflow_definition_id UUID NOT NULL,

    name VARCHAR(200) NOT NULL,

    type atomic_condition_type NOT NULL,

    CONSTRAINT fk_atomic_condition_workflow
        FOREIGN KEY(workflow_definition_id)
            REFERENCES workflow_definition(id)
            ON DELETE CASCADE,

    CONSTRAINT uq_atomic_condition_name
        UNIQUE(workflow_definition_id,name)
);

------------------------------------------------------------
-- CONDITION <-> ATOMIC CONDITION
------------------------------------------------------------

CREATE TABLE condition_atomic_condition
(
    condition_definition_id UUID NOT NULL,

    atomic_condition_id UUID NOT NULL,

    PRIMARY KEY
        (
         condition_definition_id,
         atomic_condition_id
            ),

    CONSTRAINT fk_condition_atomic_condition
        FOREIGN KEY(condition_definition_id)
            REFERENCES condition_definition(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_atomic_condition
        FOREIGN KEY(atomic_condition_id)
            REFERENCES atomic_condition(id)
            ON DELETE CASCADE
);

------------------------------------------------------------
-- STATE -> CONDITION
------------------------------------------------------------

CREATE TABLE state_definition_condition
(
    state_definition_id UUID PRIMARY KEY,

    condition_definition_id UUID NOT NULL,

    CONSTRAINT fk_state_condition_state
        FOREIGN KEY(state_definition_id)
            REFERENCES state_definition(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_state_condition_condition
        FOREIGN KEY(condition_definition_id)
            REFERENCES condition_definition(id)
            ON DELETE RESTRICT
);

------------------------------------------------------------
-- STATE IN CONDITION
------------------------------------------------------------

CREATE TABLE state_in_condition
(
    atomic_condition_id UUID PRIMARY KEY,

    task_definition_id UUID NOT NULL,

    state_definition_id UUID NOT NULL,

    CONSTRAINT fk_state_in_atomic
        FOREIGN KEY(atomic_condition_id)
            REFERENCES atomic_condition(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_state_in_task
        FOREIGN KEY(task_definition_id)
            REFERENCES task_definition(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_state_in_state
        FOREIGN KEY(state_definition_id)
            REFERENCES state_definition(id)
            ON DELETE CASCADE
);

------------------------------------------------------------
-- STATE INPUT CONDITION
------------------------------------------------------------

CREATE TABLE state_input_condition
(
    atomic_condition_id UUID PRIMARY KEY,

    task_definition_id UUID NOT NULL,

    state_definition_id UUID NOT NULL,

    json_path TEXT NOT NULL,

    comparison_operator comparison_operator NOT NULL,

    expected_value JSONB NOT NULL,

    CONSTRAINT fk_state_input_atomic
        FOREIGN KEY(atomic_condition_id)
            REFERENCES atomic_condition(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_state_input_task
        FOREIGN KEY(task_definition_id)
            REFERENCES task_definition(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_state_input_state
        FOREIGN KEY(state_definition_id)
            REFERENCES state_definition(id)
            ON DELETE CASCADE
);

------------------------------------------------------------
-- STATE OUTPUT CONDITION
------------------------------------------------------------

CREATE TABLE state_output_condition
(
    atomic_condition_id UUID PRIMARY KEY,

    task_definition_id UUID NOT NULL,

    state_definition_id UUID NOT NULL,

    json_path TEXT NOT NULL,

    comparison_operator comparison_operator NOT NULL,

    expected_value JSONB NOT NULL,

    CONSTRAINT fk_state_output_atomic
        FOREIGN KEY(atomic_condition_id)
            REFERENCES atomic_condition(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_state_output_task
        FOREIGN KEY(task_definition_id)
            REFERENCES task_definition(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_state_output_state
        FOREIGN KEY(state_definition_id)
            REFERENCES state_definition(id)
            ON DELETE CASCADE
);

------------------------------------------------------------
-- WORKFLOW VARIABLE CONDITION
------------------------------------------------------------

CREATE TABLE workflow_variable_condition
(
    atomic_condition_id UUID PRIMARY KEY,

    variable_definition_id UUID NOT NULL,

    comparison_operator comparison_operator NOT NULL,

    expected_value JSONB NOT NULL,

    CONSTRAINT fk_workflow_variable_atomic
        FOREIGN KEY(atomic_condition_id)
            REFERENCES atomic_condition(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_workflow_variable_definition
        FOREIGN KEY(variable_definition_id)
            REFERENCES variable_definition(id)
            ON DELETE CASCADE
);

------------------------------------------------------------
-- CIRCULAR FOREIGN KEYS
------------------------------------------------------------

ALTER TABLE workflow_definition
    ADD CONSTRAINT fk_workflow_start_task
        FOREIGN KEY(start_task_definition_id)
            REFERENCES task_definition(id)
            ON DELETE RESTRICT;

ALTER TABLE workflow_definition
    ADD CONSTRAINT fk_workflow_start_state
        FOREIGN KEY(start_state_definition_id)
            REFERENCES state_definition(id)
            ON DELETE RESTRICT;

------------------------------------------------------------
-- DATA REFERENCE VALIDATION
------------------------------------------------------------

ALTER TABLE data_reference
    ADD CONSTRAINT chk_data_reference
        CHECK
            (
            (
                type = 'CONSTANT'
                    AND constant_value IS NOT NULL
                    AND variable_definition_id IS NULL
                )

                OR

            (
                type = 'WORKFLOW_VARIABLE'
                    AND variable_definition_id IS NOT NULL
                    AND constant_value IS NULL
                )

                OR

            (
                type IN ('STATE_INPUT','STATE_OUTPUT')
                    AND task_definition_id IS NOT NULL
                    AND state_definition_id IS NOT NULL
                    AND json_path IS NOT NULL
                )
            );

------------------------------------------------------------
-- INDEXES
------------------------------------------------------------

CREATE INDEX idx_variable_definition_workflow
    ON variable_definition(workflow_definition_id);

CREATE INDEX idx_task_definition_workflow
    ON task_definition(workflow_definition_id);

CREATE INDEX idx_task_authorization_task
    ON task_definition_authorization(task_definition_id);

CREATE INDEX idx_state_definition_task
    ON state_definition(task_definition_id);

CREATE INDEX idx_input_definition_state
    ON input_definition(state_definition_id);

CREATE INDEX idx_output_definition_state
    ON output_definition(state_definition_id);

CREATE INDEX idx_script_definition_state
    ON script_definition(state_definition_id);

CREATE INDEX idx_script_environment_variable_script
    ON script_environment_variable(script_definition_id);

CREATE INDEX idx_data_reference_task
    ON data_reference(task_definition_id);

CREATE INDEX idx_data_reference_state
    ON data_reference(state_definition_id);

CREATE INDEX idx_data_reference_variable
    ON data_reference(variable_definition_id);

CREATE INDEX idx_input_binding_definition
    ON input_binding(input_definition_id);

CREATE INDEX idx_input_binding_reference
    ON input_binding(data_reference_id);

CREATE INDEX idx_condition_workflow
    ON condition_definition(workflow_definition_id);

CREATE INDEX idx_atomic_condition_workflow
    ON atomic_condition(workflow_definition_id);

CREATE INDEX idx_condition_atomic_condition
    ON condition_atomic_condition(condition_definition_id);

CREATE INDEX idx_atomic_condition_lookup
    ON condition_atomic_condition(atomic_condition_id);

CREATE INDEX idx_state_definition_condition
    ON state_definition_condition(condition_definition_id);

CREATE INDEX idx_state_in_task
    ON state_in_condition(task_definition_id);

CREATE INDEX idx_state_in_state
    ON state_in_condition(state_definition_id);

CREATE INDEX idx_state_input_task
    ON state_input_condition(task_definition_id);

CREATE INDEX idx_state_input_state
    ON state_input_condition(state_definition_id);

CREATE INDEX idx_state_output_task
    ON state_output_condition(task_definition_id);

CREATE INDEX idx_state_output_state
    ON state_output_condition(state_definition_id);

CREATE INDEX idx_workflow_variable_condition
    ON workflow_variable_condition(variable_definition_id);
------------------------------------------------------------
-- RUNTIME ENUMS
------------------------------------------------------------

CREATE TYPE workflow_run_status AS ENUM
    (
    'CREATED',
    'RUNNING',
    'COMPLETED',
    'FAILED',
    'CANCELLED'
);

CREATE TYPE task_run_status AS ENUM
    (
    'WAITING',
    'READY',
    'RUNNING',
    'COMPLETED',
    'FAILED',
    'CANCELLED'
);

CREATE TYPE state_run_status AS ENUM
    (
    'WAITING',
    'ACTIVE',
    'COMPLETED',
    'FAILED',
    'CANCELLED'
);

------------------------------------------------------------
-- WORKFLOW RUN
------------------------------------------------------------

CREATE TABLE workflow_run
(
    id UUID PRIMARY KEY,

    workflow_definition_id UUID NOT NULL,

    status workflow_run_status NOT NULL,

    started_at TIMESTAMP,

    completed_at TIMESTAMP,

    CONSTRAINT fk_workflow_run_definition
        FOREIGN KEY (workflow_definition_id)
            REFERENCES workflow_definition(id)
            ON DELETE RESTRICT
);

------------------------------------------------------------
-- RUNTIME VARIABLES
------------------------------------------------------------

CREATE TABLE variable
(
    id UUID PRIMARY KEY,

    workflow_run_id UUID NOT NULL,

    variable_definition_id UUID NOT NULL,

    value JSONB,

    CONSTRAINT fk_variable_workflow_run
        FOREIGN KEY (workflow_run_id)
            REFERENCES workflow_run(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_variable_definition
        FOREIGN KEY (variable_definition_id)
            REFERENCES variable_definition(id)
            ON DELETE RESTRICT,

    CONSTRAINT uq_variable
        UNIQUE(workflow_run_id, variable_definition_id)
);

------------------------------------------------------------
-- TASK RUN
------------------------------------------------------------

CREATE TABLE task_run
(
    id UUID PRIMARY KEY,

    workflow_run_id UUID NOT NULL,

    task_definition_id UUID NOT NULL,

    current_state_definition_id UUID,

    retry_count INTEGER NOT NULL DEFAULT 0,

    status task_run_status NOT NULL,

    started_at TIMESTAMP,

    completed_at TIMESTAMP,

    CONSTRAINT fk_task_run_workflow
        FOREIGN KEY (workflow_run_id)
            REFERENCES workflow_run(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_task_run_definition
        FOREIGN KEY (task_definition_id)
            REFERENCES task_definition(id)
            ON DELETE RESTRICT,

    CONSTRAINT fk_current_state_definition
        FOREIGN KEY (current_state_definition_id)
            REFERENCES state_definition(id)
            ON DELETE SET NULL,

    CONSTRAINT uq_task_run
        UNIQUE(workflow_run_id, task_definition_id)
);

------------------------------------------------------------
-- STATE RUN
------------------------------------------------------------

CREATE TABLE state_run
(
    id UUID PRIMARY KEY,

    task_run_id UUID NOT NULL,

    state_definition_id UUID NOT NULL,

    status state_run_status NOT NULL,

    entered_at TIMESTAMP,

    exited_at TIMESTAMP,

    input JSONB,

    output JSONB,

    CONSTRAINT fk_state_run_task
        FOREIGN KEY (task_run_id)
            REFERENCES task_run(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_state_run_definition
        FOREIGN KEY (state_definition_id)
            REFERENCES state_definition(id)
            ON DELETE RESTRICT
);

------------------------------------------------------------
-- SCRIPT EXECUTION
------------------------------------------------------------

CREATE TABLE script_execution
(
    id UUID PRIMARY KEY,

    state_run_id UUID NOT NULL,

    script_definition_id UUID NOT NULL,

    attempt_number INTEGER NOT NULL,

    started_at TIMESTAMP,

    completed_at TIMESTAMP,

    exit_code INTEGER,

    stdout_location TEXT,

    stderr_location TEXT,

    CONSTRAINT fk_script_execution_state
        FOREIGN KEY (state_run_id)
            REFERENCES state_run(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_script_execution_definition
        FOREIGN KEY (script_definition_id)
            REFERENCES script_definition(id)
            ON DELETE RESTRICT,

    CONSTRAINT uq_script_attempt
        UNIQUE(state_run_id, attempt_number)
);

------------------------------------------------------------
-- INDEXES
------------------------------------------------------------

CREATE INDEX idx_workflow_run_definition
    ON workflow_run(workflow_definition_id);

CREATE INDEX idx_variable_workflow_run
    ON variable(workflow_run_id);

CREATE INDEX idx_variable_definition
    ON variable(variable_definition_id);

CREATE INDEX idx_task_run_workflow
    ON task_run(workflow_run_id);

CREATE INDEX idx_task_run_definition
    ON task_run(task_definition_id);

CREATE INDEX idx_task_run_current_state
    ON task_run(current_state_definition_id);

CREATE INDEX idx_state_run_task
    ON state_run(task_run_id);

CREATE INDEX idx_state_run_definition
    ON state_run(state_definition_id);

CREATE INDEX idx_script_execution_state
    ON script_execution(state_run_id);

CREATE INDEX idx_script_execution_definition
    ON script_execution(script_definition_id);

CREATE INDEX idx_script_execution_attempt
    ON script_execution(state_run_id, attempt_number);


