package org.planqk.atlas.core.repository;

import java.util.Optional;
import java.util.UUID;

import org.planqk.atlas.core.model.AlgorithmRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repository to access {@link AlgorithmRelation}s available in the data base with
 * different queries.
 */
@RepositoryRestResource(exported = false)
public interface AlgorithmRelationRepository extends JpaRepository<AlgorithmRelation, UUID> {

	Optional<AlgorithmRelation> findBySourceAlgorithmIdAndTargetAlgorithmIdAndAlgoRelationTypeId(UUID sourceAlgId, UUID targetAlgId, UUID algoRelationTypeId);
	
}
