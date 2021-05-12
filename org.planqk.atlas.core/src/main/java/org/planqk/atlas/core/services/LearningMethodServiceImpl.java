/*******************************************************************************
 * Copyright (c) 2021 the qc-atlas contributors.
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

import java.util.Objects;
import java.util.UUID;

import org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException;
import org.planqk.atlas.core.model.LearningMethod;
import org.planqk.atlas.core.repository.LearningMethodRepository;
import org.planqk.atlas.core.util.ServiceUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class LearningMethodServiceImpl implements LearningMethodService {

    private final LearningMethodRepository learningMethodRepository;

    @Override
    public LearningMethod create(@NonNull LearningMethod learningMethod) {
        return learningMethodRepository.save(learningMethod);
    }

    @Override
    public Page<LearningMethod> findAll(Pageable pageable, String search) {
        if (!Objects.isNull(search) && !search.isEmpty()) {
            return learningMethodRepository.findByNameContainingIgnoreCase(search, pageable);
        }
        return learningMethodRepository.findAll(pageable);
    }

    @Override
    public LearningMethod findById(UUID learningMethodId) {
        return ServiceUtils.findById(learningMethodId, LearningMethod.class, learningMethodRepository);
    }

    @Override
    public LearningMethod update(LearningMethod learningMethod) {
        final LearningMethod persistedLearningMethod = findById(learningMethod.getId());

        persistedLearningMethod.setName(learningMethod.getName());

        return create(persistedLearningMethod);
    }

    @Override
    public void delete(UUID learningMethodId) {
        final LearningMethod learningMethod = findById(learningMethodId);

        if (learningMethod.getAlgorithms().size() > 0) {
            throw new EntityReferenceConstraintViolationException("Cannot delete LearningMethod with ID \"" + learningMethodId +
                    "\". It is used by existing algorithms!");
        }

        learningMethodRepository.deleteById(learningMethodId);
    }
}
