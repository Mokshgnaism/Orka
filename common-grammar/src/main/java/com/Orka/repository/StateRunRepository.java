package com.Orka.repository;

import com.Orka.entities.runtime.StateRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface StateRunRepository extends JpaRepository<StateRun, UUID> {
}
