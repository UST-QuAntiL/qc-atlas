package org.planqk.atlas.core.services;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.ProblemType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProblemTypeService {

	ProblemType save(ProblemType problemType);
	
	ProblemType update(UUID id, ProblemType problemType);
	
	void delete(UUID id);
	
	Optional<ProblemType> findById(UUID id);
	
	Optional<ProblemType> findByName(String name);
	
	Page<ProblemType> findAll(Pageable pageable);
	
	Set<ProblemType> createOrUpdateAll(Set<ProblemType> problemTypes);
	
}
