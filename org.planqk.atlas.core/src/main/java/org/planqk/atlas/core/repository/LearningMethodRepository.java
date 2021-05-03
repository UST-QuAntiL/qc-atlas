package org.planqk.atlas.core.repository;

import java.util.UUID;

import org.planqk.atlas.core.model.LearningMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository to access {@link LearningMethod}s available in the data base with different queries.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface LearningMethodRepository extends JpaRepository<LearningMethod, UUID> {

    Page<LearningMethod> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT lm " +
            "FROM LearningMethod lm " +
            "JOIN lm.algorithms algos " +
            "WHERE algos.id = :algoId")
    Page<LearningMethod> findLearningMethodByAlgorithmId(@Param("algoId") UUID algorithmId, Pageable pageable);
}
