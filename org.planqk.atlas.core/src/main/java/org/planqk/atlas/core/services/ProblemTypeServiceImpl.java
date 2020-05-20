package org.planqk.atlas.core.services;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.repository.ProblemTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProblemTypeServiceImpl implements ProblemTypeService {

	@Autowired
	private ProblemTypeRepository repo;

	@Override
	public ProblemType save(ProblemType problemType) {
		return repo.save(problemType);
	}

	@Override
	public ProblemType update(UUID id, ProblemType problemType) {
		// Check for type in database
		Optional<ProblemType> typeOpt = findById(id);
		// If Type exists
		if (typeOpt.isPresent()) {
			// Update fields
			ProblemType persistedType = typeOpt.get();
			persistedType.setName(problemType.getName());
			persistedType.setParentProblemType(problemType.getParentProblemType());
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
	public Optional<ProblemType> findById(UUID id) {
		return Objects.isNull(id) ? Optional.empty() : repo.findById(id);
	}

	@Override
	public Optional<ProblemType> findByName(String name) {
		return Objects.isNull(name) ? Optional.empty() : repo.findByName(name);
	}

	@Override
	public Page<ProblemType> findAll(Pageable pageable) {
		return repo.findAll(pageable);
	}

	@Override
	public Set<ProblemType> createOrUpdateAll(Set<ProblemType> algorithmTypes) {
		Set<ProblemType> types = algorithmTypes;
		// Go Iterate all types
		for (ProblemType type : algorithmTypes) {
			types.remove(type);
			// Check for type in database
			Optional<ProblemType> typeOpt = findById(type.getId());
			// If Type exists
			if (typeOpt.isPresent()) {
				// Update fields
				ProblemType persistedType = typeOpt.get();
				persistedType.setName(type.getName());
				persistedType.setParentProblemType(type.getParentProblemType());
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
