package org.planqk.atlas.core.services;

import java.util.Optional;

import org.planqk.atlas.core.model.ProblemType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProblemTypeService {

	ProblemType save(ProblemType problemType);
	
	Optional<ProblemType> getById(Long id);
	
	Optional<ProblemType> getByName(String name);
	
	Page<ProblemType> getAll(Pageable pageable);
	
}
