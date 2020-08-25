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

import org.planqk.atlas.core.model.AlgorithmRelation;
import org.planqk.atlas.core.repository.AlgorithmRelationRepository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AlgoRelationServiceImpl implements AlgoRelationService {

    private final AlgorithmRelationRepository algorithmRelationRepository;

    @Override
    @Transactional
    public AlgorithmRelation save(AlgorithmRelation algorithmRelation) {
        return algorithmRelationRepository.save(algorithmRelation);
    }

    @Override
    public AlgorithmRelation findById(UUID algorithmRelationId) {
        return algorithmRelationRepository.findById(algorithmRelationId).orElseThrow(NoSuchElementException::new);
    }

    @Override
    @Transactional
    public AlgorithmRelation update(AlgorithmRelation algorithmRelation) {
        AlgorithmRelation persistedAlgorithmRelation = findById(algorithmRelation.getId());

        persistedAlgorithmRelation.setDescription(algorithmRelation.getDescription());
        persistedAlgorithmRelation.setAlgoRelationType(algorithmRelation.getAlgoRelationType());

        return algorithmRelationRepository.save(algorithmRelation);
    }

    @Override
    @Transactional
    public void delete(UUID algorithmRelationId) {
        algorithmRelationRepository.deleteById(algorithmRelationId);
    }
}
