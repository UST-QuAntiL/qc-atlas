package org.planqk.atlas.core.services;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.model.exceptions.SqlConsistencyException;
import org.planqk.atlas.core.repository.AlgorithmRepository;
import org.planqk.atlas.core.repository.ProblemTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProblemTypeServiceImpl implements ProblemTypeService {

	private static final Logger LOG = LoggerFactory.getLogger(ProblemTypeServiceImpl.class);

	@Autowired
	private ProblemTypeRepository repo;
	@Autowired
	private AlgorithmRepository algRepo;

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
	public void delete(UUID id) throws SqlConsistencyException {
		if (algRepo.countAlgorithmsUsingProblemType(id) > 0) {
			LOG.info("Trying to delete ProblemType that is used by at least 1 algorithm");
			throw new SqlConsistencyException("Cannot delete ProbemType, since it is used by existing algorithms!");
		}
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
		Set<ProblemType> types = new HashSet<>();
		// Go Iterate all types
		for (ProblemType type : algorithmTypes) {
			// Check for type in database
			Optional<ProblemType> typeOpt = findById(type.getId());
			// If Type exists
			if (typeOpt.isPresent()) {
				// Update fields
				ProblemType persistedType = typeOpt.get();
				persistedType.setName(type.getName());
				persistedType.setParentProblemType(type.getParentProblemType());
				// Reference database type to set
				types.add(save(persistedType));
			}
			// If Type does not exist --> Create one
			types.add(save(type));
		}

		return types;
	}

}
