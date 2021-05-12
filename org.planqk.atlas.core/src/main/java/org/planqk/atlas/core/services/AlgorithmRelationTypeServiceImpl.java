/*******************************************************************************
 * Copyright (c) 2020 the qc-atlas contributors.
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

import java.util.UUID;

import org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException;
import org.planqk.atlas.core.model.AlgorithmRelationType;
import org.planqk.atlas.core.repository.AlgorithmRelationRepository;
import org.planqk.atlas.core.repository.AlgorithmRelationTypeRepository;
import org.planqk.atlas.core.util.ServiceUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class AlgorithmRelationTypeServiceImpl implements AlgorithmRelationTypeService {

    private final AlgorithmRelationTypeRepository algorithmRelationTypeRepository;

    private final AlgorithmRelationRepository algorithmRelationRepository;

    @Override
    @Transactional
    public AlgorithmRelationType create(@NonNull AlgorithmRelationType algorithmRelationType) {
        return algorithmRelationTypeRepository.save(algorithmRelationType);
    }

    @Override
    public Page<AlgorithmRelationType> findAll(@NonNull Pageable pageable) {
        return algorithmRelationTypeRepository.findAll(pageable);
    }

    @Override
    public AlgorithmRelationType findById(@NonNull UUID algorithmRelationTypeId) {
        return ServiceUtils.findById(algorithmRelationTypeId, AlgorithmRelationType.class, algorithmRelationTypeRepository);
    }

    @Override
    @Transactional
    public AlgorithmRelationType update(@NonNull AlgorithmRelationType algorithmRelationType) {
        final var persistedAlgorithmRelationType = findById(algorithmRelationType.getId());

        persistedAlgorithmRelationType.setName(algorithmRelationType.getName());

        return algorithmRelationTypeRepository.save(persistedAlgorithmRelationType);
    }

    @Override
    @Transactional
    public void delete(@NonNull UUID algorithmRelationTypeId) {
        ServiceUtils.throwIfNotExists(algorithmRelationTypeId, AlgorithmRelationType.class, algorithmRelationTypeRepository);

        if (algorithmRelationRepository.countByAlgorithmRelationTypeId(algorithmRelationTypeId) > 0) {
            throw new EntityReferenceConstraintViolationException("AlgorithmRelationType with ID \""
                    + algorithmRelationTypeId + "\" cannot be deleted, because it is still in use");
        }

        algorithmRelationTypeRepository.deleteById(algorithmRelationTypeId);
    }
}
