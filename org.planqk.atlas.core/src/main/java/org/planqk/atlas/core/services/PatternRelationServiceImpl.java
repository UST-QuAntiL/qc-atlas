/*******************************************************************************
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
import java.util.UUID;

import javax.transaction.Transactional;

import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.core.repository.AlgorithmRepository;
import org.planqk.atlas.core.repository.PatternRelationRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class PatternRelationServiceImpl implements PatternRelationService {

    private static final String NO_RELATION_ERROR = "Pattern relation does not exist!";

    private final PatternRelationRepository patternRelationRepository;
    private final PatternRelationTypeService patternRelationTypeService;
    private final AlgorithmRepository algorithmRepository;

    @Override
    @Transactional
    public PatternRelation create(PatternRelation patternRelation) {
        if (patternRelation.getAlgorithm() == null
                || patternRelation.getAlgorithm().getId() == null) {
            throw new NoSuchElementException("The given algorithm is invalid");
        }

        patternRelation.setAlgorithm(algorithmRepository.findById(
                patternRelation.getAlgorithm().getId())
                .orElseThrow(() -> new NoSuchElementException(
                        "No Algorithm with given ID \"" + patternRelation.getAlgorithm().getId() + "\" was found")));

        patternRelation.setPatternRelationType(
                patternRelationTypeService.findById(patternRelation.getPatternRelationType().getId()));

        return patternRelationRepository.save(patternRelation);
    }

    @Override
    public PatternRelation findById(UUID patternRelationId) {
        return patternRelationRepository.findById(patternRelationId).orElseThrow(() -> new NoSuchElementException(NO_RELATION_ERROR));
    }

    @Override
    public Page<PatternRelation> findAll(Pageable pageable) {
        return patternRelationRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public PatternRelation update(PatternRelation patternRelation) {
        PatternRelation persistedPatternRelation = findById(patternRelation.getId());

        persistedPatternRelation.setPattern(patternRelation.getPattern());
        persistedPatternRelation.setDescription(patternRelation.getDescription());
        persistedPatternRelation.setPatternRelationType(patternRelation.getPatternRelationType());

        return patternRelationRepository.save(persistedPatternRelation);
    }

    @Override
    @Transactional
    public void delete(UUID patternRelationId) {
        patternRelationRepository.deleteById(patternRelationId);
    }
}
