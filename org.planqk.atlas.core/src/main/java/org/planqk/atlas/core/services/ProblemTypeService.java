package org.planqk.atlas.core.services;

import java.util.Optional;
import java.util.Set;

import org.planqk.atlas.core.model.ProblemType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProblemTypeService {

	ProblemType save(ProblemType problemType);
	
	ProblemType update(Long id, ProblemType problemType);
	
	void delete(Long id);
	
	Optional<ProblemType> findById(Long id);
	
	Optional<ProblemType> findByName(String name);
	
	Page<ProblemType> findAll(Pageable pageable);
	
	Set<ProblemType> createOrUpdateAll(Set<ProblemType> problemTypes);
	
}
