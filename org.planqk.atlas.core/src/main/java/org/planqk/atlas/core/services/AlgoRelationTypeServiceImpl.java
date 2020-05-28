package org.planqk.atlas.core.services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.AlgoRelationType;
import org.planqk.atlas.core.model.exceptions.NotFoundException;
import org.planqk.atlas.core.model.exceptions.SqlConsistencyException;
import org.planqk.atlas.core.repository.AlgoRelationTypeRepository;
import org.planqk.atlas.core.repository.AlgorithmRelationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AlgoRelationTypeServiceImpl implements AlgoRelationTypeService {

	private static final Logger LOG = LoggerFactory.getLogger(AlgoRelationType.class);
	
	@Autowired
	private AlgoRelationTypeRepository repo;
	@Autowired AlgorithmRelationRepository algorithmRelationRepository;

	@Override
	public AlgoRelationType save(AlgoRelationType algoRelationType) {
		return repo.save(algoRelationType);
	}

	@Override
	public AlgoRelationType update(UUID id, AlgoRelationType algoRelationType) throws NotFoundException {
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
		LOG.info("Trying to update AlgoRelationType which does not exist.");
		throw new NotFoundException("Cannot update AlgoRelationType since it could not be found.");
	}

	@Override
	public void delete(UUID id) throws SqlConsistencyException {
		if (algorithmRelationRepository.countRelationsUsingRelationType(id) > 0) {
			LOG.info("Trying to delete algoRelationType that is used in at least 1 algorithmRelation.");
			throw new SqlConsistencyException("Cannot delete algoRelationType, since it is used by existing algorithmRelations!");
		}
		repo.deleteById(id);
	}

	@Override
	public Optional<AlgoRelationType> findById(UUID id) {
		return Objects.isNull(id) ? Optional.empty() : repo.findById(id);
	}

	@Override
	public Optional<List<AlgoRelationType>> findByName(String name) {
		return Objects.isNull(name) ? Optional.empty() : repo.findByName(name);
	}

	@Override
	public Page<AlgoRelationType> findAll(Pageable pageable) {
		return repo.findAll(pageable);
	}

}
