package org.planqk.atlas.core.services;

import java.util.UUID;

import javax.transaction.Transactional;

import org.planqk.atlas.core.model.PatternRelation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PatternRelationService {

    @Transactional
    PatternRelation save(PatternRelation relation);

    PatternRelation findById(UUID id);

    Page<PatternRelation> findAll(Pageable pageable);

    @Transactional
    PatternRelation update(UUID id, PatternRelation relation);

    @Transactional
    void deleteById(UUID id);
}
