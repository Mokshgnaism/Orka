CREATE TABLE task_definition IF NOT EXISTS (
    id VARCHAR(70) primary key,
    workflow_definition_id VARCHAR(70),
    name VARCHAR(250),
    description VARCHAR(500),
)
CREATE TABLE workflow_definition(
    id VARCHAR(70) PRIMARY KEY,
    name VARCHAR(50) NOT NULL ,
    description VARCHAR( 500),
    version INT NOT NULL default 1,
    creator_username VARCHAR(150),
    start_task_definition_id VARCHAR(70),
    start_state_definition_id VARCHAR(70),
)
CREATE TABLE state_definition(
    id ,
    taks_definition_id,
    name,
    priority,
    ORKA_INTENAL_STATE,
    condition_id,
    input_definition_id,
    output_definition_id
)

create table input_definition(
    id,
    json_schema,
--     will have a 1:n schema from the input bindings thing . neatly .
)

create table output_definition(
    id,
    json_schema
)

create table input_binding(
    id,
    destination_json_path,
    data_reference_id,
    type enum [s_i,s_o,const,work_var]
)
-- input binding has a destination_json_path right how we will implement stuff if
-- we get data from the *_input_referernce (iut will contain one jsonPath or it will provide data (it implements a method named as get ., and we will have method inside the input definition .
-- we wi

create table state_input_reference(
    task_definition_id,
    state_definition_od,
    json_path
)

create table state_output_reference(
                                       task_definition_id,
                                       state_definition_od,
                                       json_path
)
create table workflow_variable_reference(
    variable_name
)
create table constant_reference(
    json_value
)
create table

