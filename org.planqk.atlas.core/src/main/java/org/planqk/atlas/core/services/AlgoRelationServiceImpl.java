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
import java.util.Optional;
import java.util.UUID;

import org.planqk.atlas.core.model.AlgorithmRelation;
import org.planqk.atlas.core.repository.AlgorithmRelationRepository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AlgoRelationServiceImpl implements AlgoRelationService {

    private final AlgorithmRelationRepository algorithmRelationRepository;

    @Override
    public AlgorithmRelation save(AlgorithmRelation relation) {
        return algorithmRelationRepository.save(relation);
    }

    @Override
    public AlgorithmRelation update(UUID id, AlgorithmRelation relation) {
        AlgorithmRelation persistedRelation = algorithmRelationRepository.findById(id).orElseThrow(NoSuchElementException::new);

        return algorithmRelationRepository.save(relation);
    }

    @Override
    public void delete(UUID id) {
        Optional<AlgorithmRelation> relationOptional = algorithmRelationRepository.findById(id);
        if (!relationOptional.isPresent()) {
            return;
        }

        algorithmRelationRepository.delete(relationOptional.get());
    }

    @Override
    public AlgorithmRelation findById(UUID relationId) {
        return algorithmRelationRepository.findById(relationId).orElseThrow(NoSuchElementException::new);
    }
}
