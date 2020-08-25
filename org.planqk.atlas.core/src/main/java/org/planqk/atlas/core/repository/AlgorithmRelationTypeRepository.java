package org.planqk.atlas.core.repository;

import java.util.UUID;

import org.planqk.atlas.core.model.AlgorithmRelationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repository to access {@link AlgorithmRelationType}s available in the data base
 * with different queries.
 */
@RepositoryRestResource(exported = false)
public interface AlgorithmRelationTypeRepository extends JpaRepository<AlgorithmRelationType, UUID> {

    boolean existsAlgorithmRelationTypeById(UUID id);
}
