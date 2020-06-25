/********************************************************************************
 * Copyright (c) 2020 University of Stuttgart
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package org.planqk.atlas.core.services;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;

import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.core.repository.AlgorithmRepository;
import org.planqk.atlas.core.repository.PatternRelationRepository;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class PatternRelationServiceImpl implements PatternRelationService {

    private final String NO_RELATION_ERROR = "PatternRelation does not exist!";

    private final PatternRelationRepository repo;
    private final PatternRelationTypeService patternRelationTypeService;
    private final AlgorithmRepository algorithmRepository;

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
    public Set<PatternRelation> findByAlgorithmId(UUID algoId) {
        return repo.findByAlgorithmId(algoId);
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
        if (Objects.isNull(relation.getAlgorithm().getId()) || algorithmRepository.findById(relation.getAlgorithm().getId()).isEmpty()) {
            throw new NoSuchElementException("Algorithm for pattern relation does not exist!");
        }

        relation.setPatternRelationType(patternRelationTypeService.createOrGet(relation.getPatternRelationType()));
    }
}
