package org.planqk.atlas.core.repository;

import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.AlgoRelationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repository to access {@link AlgoRelationType}s available in the data base with
 * different queries.
 */
@RepositoryRestResource(exported = false)
public interface AlgoRelationTypeRepository extends JpaRepository<AlgoRelationType, UUID> {

	Set<AlgoRelationType> findByName(String name);
	
}
