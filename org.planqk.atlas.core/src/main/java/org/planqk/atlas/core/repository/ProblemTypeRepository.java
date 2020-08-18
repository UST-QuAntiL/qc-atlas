package org.planqk.atlas.core.repository;

import java.util.Optional;
import java.util.UUID;

import org.planqk.atlas.core.model.ProblemType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repository to access {@link ProblemType}s available in the data base with
 * different queries.
 */
@RepositoryRestResource(exported = false)
public interface ProblemTypeRepository extends JpaRepository<ProblemType, UUID> {

    Optional<ProblemType> findByName(String name);

    @Query("SELECT pt FROM ProblemType pt JOIN pt.algorithms algos WHERE algos.id = :algoid")
    Page<ProblemType> findProblemTypesByAlgorithmId(@Param("algoid") UUID id, Pageable p);
}
