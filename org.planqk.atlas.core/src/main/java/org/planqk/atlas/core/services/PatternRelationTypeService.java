package org.planqk.atlas.core.services;

import java.util.UUID;

import org.planqk.atlas.core.model.PatternRelationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PatternRelationTypeService {

    PatternRelationType save(PatternRelationType type);

    PatternRelationType findById(UUID id);

    Page<PatternRelationType> findAll(Pageable pageable);

    PatternRelationType update(UUID id, PatternRelationType type);

    void deleteById(UUID id);

}
