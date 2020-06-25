package org.planqk.atlas.core.repository;

import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.PatternRelation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repository to access {@link PatternRelation}s available in the data base with different queries.
 */
@RepositoryRestResource(exported = false)
public interface PatternRelationRepository extends JpaRepository<PatternRelation, UUID> {

    long countByPatternRelationTypeId(UUID id);

    Set<PatternRelation> findByAlgorithmId(UUID algoId);
}
