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

import org.planqk.atlas.core.model.AlgoRelationType;
import org.planqk.atlas.core.model.exceptions.ConsistencyException;
import org.planqk.atlas.core.repository.AlgoRelationTypeRepository;
import org.planqk.atlas.core.repository.AlgorithmRelationRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class AlgoRelationTypeServiceImpl implements AlgoRelationTypeService {

    private final AlgoRelationTypeRepository algoRelationTypeRepository;
    private final AlgorithmRelationRepository algorithmRelationRepository;

    @Override
    @Transactional
    public AlgoRelationType create(AlgoRelationType algoRelationType) {
        return algoRelationTypeRepository.save(algoRelationType);
    }

    @Override
    public Page<AlgoRelationType> findAll(Pageable pageable) {
        return algoRelationTypeRepository.findAll(pageable);
    }

    @Override
    public AlgoRelationType findById(UUID algorithmRelationTypeId) {
        return algoRelationTypeRepository.findById(algorithmRelationTypeId).orElseThrow(NoSuchElementException::new);
    }

    @Override
    @Transactional
    public AlgoRelationType update(AlgoRelationType algoRelationType) {
        var persistedAlgorithmRelationType = findById(algoRelationType.getId());

        persistedAlgorithmRelationType.setName(algoRelationType.getName());

        return algoRelationTypeRepository.save(persistedAlgorithmRelationType);
    }

    @Override
    @Transactional
    public void delete(UUID algorithmRelationTypeId) {
        if (algorithmRelationRepository.countByAlgoRelationType_Id(algorithmRelationTypeId) > 0) {
            throw new ConsistencyException(
                    "Cannot delete AlgorithmRelationType since it is used by existing AlgorithmRelations.");
        }

        algoRelationTypeRepository.deleteById(algorithmRelationTypeId);
    }
}
