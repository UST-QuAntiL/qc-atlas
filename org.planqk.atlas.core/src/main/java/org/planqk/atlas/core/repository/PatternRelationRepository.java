package org.planqk.atlas.core.repository;

import java.util.UUID;

import org.planqk.atlas.core.model.PatternRelation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository to access {@link PatternRelation}s available in the data base with different queries.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface PatternRelationRepository extends JpaRepository<PatternRelation, UUID> {

    long countByPatternRelationTypeId(UUID id);

    Page<PatternRelation> findByAlgorithmId(UUID algorithmId, Pageable pageable);
}
