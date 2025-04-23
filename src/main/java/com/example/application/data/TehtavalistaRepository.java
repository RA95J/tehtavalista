package com.example.application.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TehtavalistaRepository
        extends
            JpaRepository<Tehtavalista, Long>,
            JpaSpecificationExecutor<Tehtavalista> {

}
