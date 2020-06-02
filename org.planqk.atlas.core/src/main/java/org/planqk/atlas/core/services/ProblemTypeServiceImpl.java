package org.planqk.atlas.core.services;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.model.exceptions.NoContentException;
import org.planqk.atlas.core.model.exceptions.NotFoundException;
import org.planqk.atlas.core.model.exceptions.SqlConsistencyException;
import org.planqk.atlas.core.repository.AlgorithmRepository;
import org.planqk.atlas.core.repository.ProblemTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProblemTypeServiceImpl implements ProblemTypeService {

	private static final Logger LOG = LoggerFactory.getLogger(ProblemTypeServiceImpl.class);

	private static final String NOT_FOUND_MSG = "The searched problem type does not exist!";

	private ProblemTypeRepository repo;
	private AlgorithmRepository algRepo;

	@Override
	public ProblemType save(ProblemType problemType) {
		return repo.save(problemType);
	}

	@Override
	public ProblemType update(UUID id, ProblemType problemType) throws NotFoundException {
		// Get existing ProblemType if it exists
		ProblemType persistedType = findById(id);
		// Update fields
		persistedType.setName(problemType.getName());
		persistedType.setParentProblemType(problemType.getParentProblemType());
		// Save and return updated object
		return save(persistedType);
	}

	@Override
	public void delete(UUID id) throws SqlConsistencyException, NoContentException {
		if (Objects.isNull(id) || repo.findById(id).isEmpty()) {
			throw new NoContentException(NOT_FOUND_MSG);
		}
		if (algRepo.countAlgorithmsUsingProblemType(id) > 0) {
			LOG.info("Trying to delete ProblemType that is used by at least 1 algorithm");
			throw new SqlConsistencyException("Cannot delete ProbemType, since it is used by existing algorithms!");
		}
		repo.deleteById(id);
	}

	@Override
	public ProblemType findById(UUID id) throws NotFoundException {
		Optional<ProblemType> problemTypeOpt = Objects.isNull(id) ? Optional.empty() : repo.findById(id);
		if (problemTypeOpt.isPresent())
			return problemTypeOpt.get();
		throw new NotFoundException(NOT_FOUND_MSG);
	}

	@Override
	public ProblemType findByName(String name) throws NotFoundException {
		Optional<ProblemType> problemTypeOpt = Objects.isNull(name) ? Optional.empty() : repo.findByName(name);
		if (problemTypeOpt.isPresent())
			return problemTypeOpt.get();
		throw new NotFoundException(NOT_FOUND_MSG);
	}

	@Override
	public Page<ProblemType> findAll(Pageable pageable) {
		return repo.findAll(pageable);
	}

	@Override
	public Set<ProblemType> createOrUpdateAll(Set<ProblemType> problemTypes) {
		Set<ProblemType> types = new HashSet<>();
		// Go Iterate all types
		for (ProblemType type : problemTypes) {
			// Check for type in database
			try {
				ProblemType persistedType = findById(type.getId());
				persistedType.setName(type.getName());
				persistedType.setParentProblemType(type.getParentProblemType());
				// Reference database type to set
				types.add(save(persistedType));
			} catch (NotFoundException e) {
				// If Type does not exist --> Create one
				types.add(save(type));
			}
		}

		return types;
	}

}
