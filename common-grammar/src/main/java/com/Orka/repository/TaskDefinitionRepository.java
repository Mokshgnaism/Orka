package com.Orka.repository;

import com.Orka.entities.definition.TaskDefinition;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface TaskDefinitionRepository extends JpaRepository<TaskDefinition, UUID> {


    @Query("""
    select td from TaskDefinition td
    JOIN td.authorizations auth
    where auth.username = :username
""")
    List<TaskDefinition> findAuthorizedTaskDefinitions(@Param("username")String username);

}
