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
import java.util.Set;
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

    @Transactional
    @Override
    public AlgoRelationType save(AlgoRelationType algoRelationType) {
        return algoRelationTypeRepository.save(algoRelationType);
    }

    @Transactional
    @Override
    public AlgoRelationType update(UUID id, AlgoRelationType algoRelationType) {
        if (algoRelationTypeRepository.existsAlgoRelationTypeById(id)) {
            algoRelationType.setId(id);
            return save(algoRelationType);
        }
        throw new NoSuchElementException();
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        if (algorithmRelationRepository.existsById(id)) {
            throw new NoSuchElementException("AlgoRelationType with id \"" + id + "\" does not exist");
        }

        if (algorithmRelationRepository.countByAlgoRelationType_Id(id) > 0) {
            throw new ConsistencyException(
                    "Cannot delete AlgoRelationType since it is used by existing algorithmRelations.");
        }

        algoRelationTypeRepository.deleteById(id);
    }

    @Override
    public AlgoRelationType findById(UUID id) {
        return algoRelationTypeRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Set<AlgoRelationType> findByName(String name) {
        return algoRelationTypeRepository.findByName(name);
    }

    @Override
    public Page<AlgoRelationType> findAll(Pageable pageable) {
        return algoRelationTypeRepository.findAll(pageable);
    }
}
