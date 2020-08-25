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
import java.util.UUID;

import javax.transaction.Transactional;

import org.planqk.atlas.core.model.PatternRelationType;
import org.planqk.atlas.core.model.exceptions.ConsistencyException;
import org.planqk.atlas.core.repository.PatternRelationRepository;
import org.planqk.atlas.core.repository.PatternRelationTypeRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PatternRelationTypeServiceImpl implements PatternRelationTypeService {

    private static final String NO_TYPE_ERROR = "PatternRelationType does not exist!";

    private final PatternRelationTypeRepository patternRelationTypeRepository;
    private final PatternRelationRepository patternRelationRepository;

    @Override
    @Transactional
    public PatternRelationType create(PatternRelationType patternRelationType) {
        return patternRelationTypeRepository.save(patternRelationType);
    }

    @Override
    public PatternRelationType findById(UUID patternRelationTypeId) {
        return patternRelationTypeRepository.findById(patternRelationTypeId)
                .orElseThrow(() -> new NoSuchElementException(NO_TYPE_ERROR));
    }

    @Override
    public Page<PatternRelationType> findAll(Pageable pageable) {
        return patternRelationTypeRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public PatternRelationType update(PatternRelationType patternRelationType) {
        PatternRelationType persistedPatternRelationType = findById(patternRelationType.getId());

        persistedPatternRelationType.setName(patternRelationType.getName());

        return patternRelationTypeRepository.save(persistedPatternRelationType);
    }

    @Override
    @Transactional
    public void delete(UUID patternRelationTypeId) {
        if (patternRelationRepository.countByPatternRelationTypeId(patternRelationTypeId) > 0)
            throw new ConsistencyException("Can not delete an used Pattern relation type!");

        patternRelationTypeRepository.deleteById(patternRelationTypeId);
    }
}
