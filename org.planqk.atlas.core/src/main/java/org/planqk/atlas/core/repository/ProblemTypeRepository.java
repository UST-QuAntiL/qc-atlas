package org.planqk.atlas.core.repository;

import java.util.Optional;

import org.planqk.atlas.core.model.ProblemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repository to access {@link ProblemType}s available in the data base with
 * different queries.
 */
@RepositoryRestResource(exported = false)
public interface ProblemTypeRepository extends JpaRepository<ProblemType, Long> {

	Optional<ProblemType> findByName(String name);
	
}
