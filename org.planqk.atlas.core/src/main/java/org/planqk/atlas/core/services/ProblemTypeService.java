package org.planqk.atlas.core.services;

import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.model.exceptions.NoContentException;
import org.planqk.atlas.core.model.exceptions.NotFoundException;
import org.planqk.atlas.core.model.exceptions.SqlConsistencyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProblemTypeService {

	ProblemType save(ProblemType problemType);
	
	ProblemType update(UUID id, ProblemType problemType) throws NotFoundException;
	
	void delete(UUID id) throws SqlConsistencyException, NoContentException;
	
	ProblemType findById(UUID id) throws NotFoundException;
	
	ProblemType findByName(String name) throws NotFoundException;
	
	Page<ProblemType> findAll(Pageable pageable);
	
	Set<ProblemType> createOrUpdateAll(Set<ProblemType> problemTypes);
	
}
