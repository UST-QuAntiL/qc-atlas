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
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.AlgoRelationType;
import org.planqk.atlas.core.model.exceptions.ConsistencyException;
import org.planqk.atlas.core.repository.AlgoRelationTypeRepository;
import org.planqk.atlas.core.repository.AlgorithmRelationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AlgoRelationTypeServiceImpl implements AlgoRelationTypeService {

    private static final Logger LOG = LoggerFactory.getLogger(AlgoRelationType.class);

    private final AlgoRelationTypeRepository repo;
    private final AlgorithmRelationRepository algorithmRelationRepository;

    @Transactional
    @Override
    public AlgoRelationType save(AlgoRelationType algoRelationType) {
        return repo.save(algoRelationType);
    }

    @Transactional
    @Override
    public AlgoRelationType update(UUID id, AlgoRelationType algoRelationType) {
        if (repo.existsAlgoRelationTypeById(id)) {
            algoRelationType.setId(id);
            return save(algoRelationType);
        }
        throw new NoSuchElementException();
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        if (algorithmRelationRepository.countByAlgoRelationType_Id(id) > 0) {
            LOG.info("Trying to delete algoRelationType that is used in at least 1 algorithmRelation.");
            throw new ConsistencyException(
                    "Cannot delete algoRelationType since it is used by existing algorithmRelations.");
        }

        repo.deleteById(id);
    }

    @Override
    public AlgoRelationType findById(UUID id) {
        return repo.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Set<AlgoRelationType> findByName(String name) {
        return repo.findByName(name);
    }

    @Override
    public Page<AlgoRelationType> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public Optional<AlgoRelationType> findOptionalById(UUID id) {
        return repo.findById(id);
    }

}
