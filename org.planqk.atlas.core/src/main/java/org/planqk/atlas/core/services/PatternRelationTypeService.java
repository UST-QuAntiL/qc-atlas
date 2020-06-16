package org.planqk.atlas.core.services;

import java.util.UUID;

import javax.transaction.Transactional;

import org.planqk.atlas.core.model.PatternRelationType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PatternRelationTypeService {

    @Transactional
    PatternRelationType save(PatternRelationType type);

    PatternRelationType findById(UUID id);

    Page<PatternRelationType> findAll(Pageable pageable);

    @Transactional
    PatternRelationType update(UUID id, PatternRelationType type);

    @Transactional
    void deleteById(UUID id);

    @Transactional
    PatternRelationType createOrGet(PatternRelationType type);
}
