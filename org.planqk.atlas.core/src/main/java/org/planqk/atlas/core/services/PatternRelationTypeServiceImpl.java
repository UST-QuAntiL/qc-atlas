package org.planqk.atlas.core.services;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.planqk.atlas.core.model.PatternRelationType;
import org.planqk.atlas.core.model.exceptions.ConsistencyException;
import org.planqk.atlas.core.repository.PatternRelationRepository;
import org.planqk.atlas.core.repository.PatternRelationTypeRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PatternRelationTypeServiceImpl implements PatternRelationTypeService {

    private final String NO_TYPE_ERROR = "PatternRelationType does not exist!";

    private PatternRelationTypeRepository repo;
    private PatternRelationRepository patternRelationRepo;

    @Override
    public PatternRelationType save(PatternRelationType type) {
        return repo.save(type);
    }

    @Override
    public PatternRelationType findById(UUID id) {
        return repo.findById(id).orElseThrow(() -> new NoSuchElementException(NO_TYPE_ERROR));
    }

    @Override
    public Page<PatternRelationType> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public PatternRelationType update(UUID id, PatternRelationType type) {
        PatternRelationType persistedType = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException(NO_TYPE_ERROR));
        persistedType.setName(type.getName());
        return repo.save(persistedType);
    }

    @Override
    public void deleteById(UUID id) {
        if (patternRelationRepo.countByPatternRelationTypeId(id) > 0)
            throw new ConsistencyException("Can not delete a used PatternRelationType!");
        repo.deleteById(id);
    }

    @Override
    public PatternRelationType createOrGet(PatternRelationType type) {
        // Check database for type
        Optional<PatternRelationType> typeOptional = Objects.isNull(type.getId()) ? Optional.empty()
                : repo.findById(type.getId());
        if (typeOptional.isPresent()) {
            return typeOptional.get();
        }

        return save(type);
    }
}
