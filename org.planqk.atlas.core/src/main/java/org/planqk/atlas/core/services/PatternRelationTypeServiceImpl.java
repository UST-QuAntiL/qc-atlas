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
import java.util.Optional;
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

    private final String NO_TYPE_ERROR = "PatternRelationType does not exist!";

    private final PatternRelationTypeRepository repo;
    private final PatternRelationRepository patternRelationRepo;

    @Override
    @Transactional
    public PatternRelationType save(PatternRelationType type) {
        return repo.save(type);
    }

    @Override
    public PatternRelationType findById(UUID id) {
        return repo.findById(id).orElseThrow(() -> new NoSuchElementException(NO_TYPE_ERROR));
    }

    @Override
    public Page<PatternRelationType> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    @Transactional
    public PatternRelationType update(UUID id, PatternRelationType type) {
        PatternRelationType persistedType = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException(NO_TYPE_ERROR));
        persistedType.setName(type.getName());
        return repo.save(persistedType);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        if (patternRelationRepo.countByPatternRelationTypeId(id) > 0)
            throw new ConsistencyException("Can not delete a used PatternRelationType!");
        repo.deleteById(id);
    }

    @Override
    @Transactional
    public PatternRelationType createOrGet(PatternRelationType type) {
        // Check database for type
        Optional<PatternRelationType> typeOptional = Objects.isNull(type.getId()) ? Optional.empty()
                : repo.findById(type.getId());
        if (typeOptional.isPresent()) {
            return typeOptional.get();
        }

        return save(type);
    }
}
