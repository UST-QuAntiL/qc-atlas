package org.planqk.atlas.core.services;

import java.util.UUID;

import org.planqk.atlas.core.model.PatternRelation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PatternRelationService {

    PatternRelation save(PatternRelation relation);

    PatternRelation findById(UUID id);

    Page<PatternRelation> findAll(Pageable pageable);

}
