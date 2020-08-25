package org.planqk.atlas.core.repository;

import java.util.UUID;

import org.planqk.atlas.core.model.AlgorithmRelation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repository to access {@link AlgorithmRelation}s available in the data base
 * with different queries.
 */
@RepositoryRestResource(exported = false)
public interface AlgorithmRelationRepository extends JpaRepository<AlgorithmRelation, UUID> {

    Page<AlgorithmRelation> findBySourceAlgorithmIdOrTargetAlgorithmId(UUID sourceId, UUID targetId, Pageable pageable);

    long countByAlgorithmRelationTypeId(UUID algoRelationTypeId);

//    @Query("SELECT COUNT(algRel) FROM AlgorithmRelation algRel WHERE algRel.algoRelationType.id = :algoRelationTypeId")
//    long countRelationsUsingRelationType(@Param("algoRelationTypeId") UUID algoRelationTypeId);
}
