package org.planqk.atlas.core.services;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.core.repository.PatternRelationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PatternRelationServiceImpl implements PatternRelationService {

    private PatternRelationRepository repo;
    private AlgorithmService algorithmService;
    private PatternRelationTypeService patternRelationTypeService;

    @Override
    public PatternRelation save(PatternRelation relation) {
        // Validate input
        validateAlgorithm(relation);
        return repo.save(relation);
    }

    @Override
    public PatternRelation findById(UUID id) {
        return repo.findById(id).orElseThrow(() -> new NoSuchElementException("PatternRelation does not exist!"));
    }

    @Override
    public Page<PatternRelation> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    private void validateAlgorithm(PatternRelation relation) {
        // Can't create PatternRelation if Algorithm is not described
        if (Objects.isNull(relation.getAlgorithm().getId())) {
            throw new NoSuchElementException("Algorithm for pattern relation does not exist!");
        }

        relation.setAlgorithm(algorithmService.findById(relation.getAlgorithm().getId()));
        relation.setPatternRelationType(patternRelationTypeService.createOrGet(relation.getPatternRelationType()));
    }

}
