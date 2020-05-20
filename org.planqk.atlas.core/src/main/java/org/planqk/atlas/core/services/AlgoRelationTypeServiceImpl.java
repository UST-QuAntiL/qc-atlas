package org.planqk.atlas.core.services;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.AlgoRelationType;
import org.planqk.atlas.core.repository.AlgoRelationTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AlgoRelationTypeServiceImpl implements AlgoRelationTypeService {
	
	@Autowired
	private AlgoRelationTypeRepository repo;

	@Override
	public AlgoRelationType save(AlgoRelationType algoRelationType) {
		return repo.save(algoRelationType);
	}

	@Override
	public AlgoRelationType update(UUID id, AlgoRelationType algoRelationType) {
		// Check for type in database
		Optional<AlgoRelationType> typeOpt = findById(id);
		// If Type exists
		if (typeOpt.isPresent()) {
			// Update fields
			AlgoRelationType persistedType = typeOpt.get();
			persistedType.setName(algoRelationType.getName());
			// Reference database type to set
			return save(persistedType);
		}
		// TODO: Exception handling
		return null;
	}

	@Override
	public void delete(UUID id) {
		repo.deleteById(id);
	}

	@Override
	public Optional<AlgoRelationType> findById(UUID id) {
		return Objects.isNull(id) ? Optional.empty() : repo.findById(id);
	}

	@Override
	public Optional<AlgoRelationType> findByName(String name) {
		return Objects.isNull(name) ? Optional.empty() : repo.findByName(name);
	}

	@Override
	public Page<AlgoRelationType> findAll(Pageable pageable) {
		return repo.findAll(pageable);
	}

	@Override
	public Set<AlgoRelationType> createOrUpdateAll(Set<AlgoRelationType> algoRelationTypes) {
		Set<AlgoRelationType> types = algoRelationTypes;
		// Go Iterate all types
		for (AlgoRelationType type : algoRelationTypes) {
			types.remove(type);
			// Check for type in database
			Optional<AlgoRelationType> typeOpt = findById(type.getId());
			// If Type exists
			if (typeOpt.isPresent()) {
				// Update fields
				AlgoRelationType persistedType = typeOpt.get();
				persistedType.setName(type.getName());
				// Reference database type to set
				type = save(persistedType);
			} else {
				type = save(type);
			}
			types.add(type);
		}
		return types;
	}

}
