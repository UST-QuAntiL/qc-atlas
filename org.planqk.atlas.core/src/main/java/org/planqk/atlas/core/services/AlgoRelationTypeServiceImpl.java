package org.planqk.atlas.core.services;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.AlgoRelationType;
import org.planqk.atlas.core.model.exceptions.ConsistencyException;
import org.planqk.atlas.core.repository.AlgoRelationTypeRepository;
import org.planqk.atlas.core.repository.AlgorithmRelationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AlgoRelationTypeServiceImpl implements AlgoRelationTypeService {

    private static final Logger LOG = LoggerFactory.getLogger(AlgoRelationType.class);

    private AlgoRelationTypeRepository repo;
    private AlgorithmRelationRepository algorithmRelationRepository;

    @Override
    public AlgoRelationType save(AlgoRelationType algoRelationType) {
        return repo.save(algoRelationType);
    }

    @Override
    public AlgoRelationType update(UUID id, AlgoRelationType algoRelationType) {
        // Check for type in database
        AlgoRelationType persistedType = repo.findById(id).orElseThrow(NoSuchElementException::new);

        // Update Fields
        persistedType.setName(algoRelationType.getName());
        // Reference database type to set
        return save(persistedType);
    }

    @Override
    public void delete(UUID id) {
        if (algorithmRelationRepository.countRelationsUsingRelationType(id) > 0) {
            LOG.info("Trying to delete algoRelationType that is used in at least 1 algorithmRelation.");
            throw new ConsistencyException(
                    "Cannot delete algoRelationType since it is used by existing algorithmRelations.");
        }

        repo.deleteById(id);
    }

    @Override
    public AlgoRelationType findById(UUID id) {
        return repo.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Set<AlgoRelationType> findByName(String name) {
        return repo.findByName(name);
    }

    @Override
    public Page<AlgoRelationType> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public Optional<AlgoRelationType> findOptionalById(UUID id) {
        return repo.findById(id);
    }

}
