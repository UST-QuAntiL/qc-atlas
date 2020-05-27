package org.planqk.atlas.core.services;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.AlgoRelationType;
import org.planqk.atlas.core.model.exceptions.NotFoundException;
import org.planqk.atlas.core.model.exceptions.SqlConsistencyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AlgoRelationTypeService {

	AlgoRelationType save(AlgoRelationType algoRelationType);
	
	AlgoRelationType update(UUID id, AlgoRelationType algoRelationType) throws NotFoundException;
	
	void delete(UUID id) throws SqlConsistencyException;
	
	Optional<AlgoRelationType> findById(UUID id);
	
	Optional<AlgoRelationType> findByName(String name);
	
	Page<AlgoRelationType> findAll(Pageable pageable);
	
	Set<AlgoRelationType> createOrUpdateAll(Set<AlgoRelationType> algoRelationTypes);
}
