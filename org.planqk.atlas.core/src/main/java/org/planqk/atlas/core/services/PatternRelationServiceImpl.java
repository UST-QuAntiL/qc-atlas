package org.planqk.atlas.core.services;

import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.core.repository.PatternRelationRepository;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PatternRelationServiceImpl implements PatternRelationService {
    
    private PatternRelationRepository repo;
    
    @Override
    public PatternRelation save(PatternRelation relation) {
        return repo.save(relation);
    }

}
