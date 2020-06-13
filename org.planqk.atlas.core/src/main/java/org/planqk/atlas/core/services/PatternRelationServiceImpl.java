package org.planqk.atlas.core.services;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

import javax.transaction.Transactional;

import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.core.repository.PatternRelationRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PatternRelationServiceImpl implements PatternRelationService {

    private final String NO_RELATION_ERROR = "PatternRelation does not exist!";

    private PatternRelationRepository repo;
    private AlgorithmService algorithmService;
    private PatternRelationTypeService patternRelationTypeService;

    @Override
    @Transactional
    public PatternRelation save(PatternRelation relation) {
        // Validate input
        validatePatternRelation(relation);
        return repo.save(relation);
    }

    @Override
    public PatternRelation findById(UUID id) {
        return repo.findById(id).orElseThrow(() -> new NoSuchElementException(NO_RELATION_ERROR));
    }

    @Override
    public Page<PatternRelation> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    @Transactional
    public PatternRelation update(UUID id, PatternRelation relation) {
        PatternRelation persistedRelation = repo.findById(id).orElseThrow(() -> new NoSuchElementException(NO_RELATION_ERROR));
        // Update fields
        persistedRelation.setPatternRelationType(patternRelationTypeService.createOrGet(relation.getPatternRelationType()));
        persistedRelation.setPattern(relation.getPattern());
        persistedRelation.setDescription(relation.getDescription());
        return repo.save(persistedRelation);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        repo.deleteById(id);
    }

    private void validatePatternRelation(PatternRelation relation) {
        // Can't create PatternRelation if Algorithm is not described
        if (Objects.isNull(relation.getAlgorithm().getId())) {
            throw new NoSuchElementException("Algorithm for pattern relation does not exist!");
        }

        relation.setAlgorithm(algorithmService.findById(relation.getAlgorithm().getId()));
        relation.setPatternRelationType(patternRelationTypeService.createOrGet(relation.getPatternRelationType()));
    }
}
