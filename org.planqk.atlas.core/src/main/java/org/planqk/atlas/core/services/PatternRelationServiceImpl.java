package org.planqk.atlas.core.services;

import java.util.NoSuchElementException;
import java.util.Objects;

import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.core.repository.PatternRelationRepository;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PatternRelationServiceImpl implements PatternRelationService {
    
    private PatternRelationRepository repo;
    private AlgorithmService algorithmService;
    
    @Override
    public PatternRelation save(PatternRelation relation) {
        // Validate input
        validateAlgorithm(relation);
        return repo.save(relation);
    }
    
    private void validateAlgorithm(PatternRelation relation) {
        if (Objects.isNull(relation.getAlgorithm().getId())) {
            throw new NoSuchElementException("Algorithm for pattern relation does not exist!");
        }
        
        relation.setAlgorithm(algorithmService.findById(relation.getAlgorithm().getId()));
    }

}
