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

import java.util.NoSuchElementException;
import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.AlgorithmRelation;
import org.planqk.atlas.core.repository.AlgorithmRelationRepository;
import org.planqk.atlas.core.repository.AlgorithmRepository;
import org.planqk.atlas.core.util.ServiceUtils;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class AlgorithmRelationServiceImpl implements AlgorithmRelationService {

    private final AlgorithmRelationRepository algorithmRelationRepository;

    private final AlgorithmRelationTypeService algorithmRelationTypeService;

    private final AlgorithmRepository algorithmRepository;

    @Override
    @Transactional
    public AlgorithmRelation create(@NonNull AlgorithmRelation algorithmRelation) {
        algorithmRelation.setAlgorithmRelationType(
                algorithmRelationTypeService.findById(algorithmRelation.getAlgorithmRelationType().getId()));

        algorithmRelation.setSourceAlgorithm(findAlgorithmById(algorithmRelation.getSourceAlgorithm().getId()));
        algorithmRelation.setTargetAlgorithm(findAlgorithmById(algorithmRelation.getTargetAlgorithm().getId()));

        return algorithmRelationRepository.save(algorithmRelation);
    }

    @Override
    public AlgorithmRelation findById(@NonNull UUID algorithmRelationId) {
        return ServiceUtils.findById(algorithmRelationId, AlgorithmRelation.class, algorithmRelationRepository);
    }

    @Override
    @Transactional
    public AlgorithmRelation update(@NonNull AlgorithmRelation algorithmRelation) {
        AlgorithmRelation persistedAlgorithmRelation = findById(algorithmRelation.getId());

        persistedAlgorithmRelation.setAlgorithmRelationType(
                algorithmRelationTypeService.findById(algorithmRelation.getAlgorithmRelationType().getId()));
        persistedAlgorithmRelation.setDescription(algorithmRelation.getDescription());

        return algorithmRelationRepository.save(persistedAlgorithmRelation );
    }

    @Override
    @Transactional
    public void delete(@NonNull UUID algorithmRelationId) {
        ServiceUtils.throwIfNotExists(algorithmRelationId, AlgorithmRelation.class, algorithmRelationRepository);
        algorithmRelationRepository.deleteById(algorithmRelationId);
    }

    @Override
    public void checkIfAlgorithmIsInAlgorithmRelation(@NonNull UUID algorithmId, @NonNull UUID algorithmRelationId) {
        AlgorithmRelation algorithmRelation = findById(algorithmRelationId);

        if (!algorithmRelation.getSourceAlgorithm().getId().equals(algorithmId)
                && !algorithmRelation.getTargetAlgorithm().getId().equals(algorithmId)) {
            throw new NoSuchElementException("Algorithm with ID \"" + algorithmId
                    + "\" is not part of AlgorithmRelation with ID \"" + algorithmRelationId +  "\"");
        }
    }

    private Algorithm findAlgorithmById(@NonNull UUID algorithmId) {
        return ServiceUtils.findById(algorithmId, Algorithm.class, algorithmRepository);
    }
}
