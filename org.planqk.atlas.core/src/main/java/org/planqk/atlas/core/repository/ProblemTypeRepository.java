package org.planqk.atlas.core.repository;

import java.util.Optional;
import java.util.UUID;

import org.planqk.atlas.core.model.ApplicationArea;
import org.planqk.atlas.core.model.ProblemType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repository to access {@link ProblemType}s available in the data base with
 * different queries.
 */
@RepositoryRestResource(exported = false)
public interface ProblemTypeRepository extends JpaRepository<ProblemType, UUID> {

    Optional<ProblemType> findByName(String name);

    default Page<ProblemType> findAll(String search, Pageable pageable) {
        return findByNameContainingIgnoreCase(search, pageable);
    }

    Page<ProblemType> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
