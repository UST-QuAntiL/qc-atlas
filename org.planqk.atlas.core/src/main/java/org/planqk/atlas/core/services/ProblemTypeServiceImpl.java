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

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;

import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.model.exceptions.ConsistencyException;
import org.planqk.atlas.core.repository.AlgorithmRepository;
import org.planqk.atlas.core.repository.ProblemTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProblemTypeServiceImpl implements ProblemTypeService {

    private static final Logger LOG = LoggerFactory.getLogger(ProblemTypeServiceImpl.class);

    private final ProblemTypeRepository repo;
    private final AlgorithmRepository algRepo;

    @Override
    @Transactional
    public ProblemType save(ProblemType problemType) {
        return repo.save(problemType);
    }

    @Override
    @Transactional
    public ProblemType update(UUID id, ProblemType problemType) {
        // Get existing ProblemType if it exists
        ProblemType persistedType = findById(id);
        // Update fields
        persistedType.setName(problemType.getName());
        persistedType.setParentProblemType(problemType.getParentProblemType());
        // Save and return updated object
        return save(persistedType);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (algRepo.countAlgorithmsUsingProblemType(id) > 0) {
            LOG.info("Trying to delete ProblemType that is used by at least 1 algorithm");
            throw new ConsistencyException("Cannot delete ProbemType, since it is used by existing algorithms!");
        }

        repo.deleteById(id);
    }

    @Override
    public ProblemType findById(UUID id) {
        return repo.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public ProblemType findByName(String name) {
        return repo.findByName(name).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Page<ProblemType> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    @Transactional
    public Set<ProblemType> createOrUpdateAll(Set<ProblemType> problemTypes) {
        Set<ProblemType> types = new HashSet<>();
        // Go Iterate all types
        for (ProblemType type : problemTypes) {
            // Check for type in database
            Optional<ProblemType> optType = Objects.isNull(type.getId()) ? Optional.empty()
                    : repo.findById(type.getId());
            if (optType.isPresent()) {
                ProblemType persistedType = optType.get();
                persistedType.setName(persistedType.getName());
                persistedType.setParentProblemType(persistedType.getParentProblemType());
                // Reference database type to set
                types.add(save(persistedType));
            } else {
                // If Type does not exist --> Create one
                types.add(save(type));
            }
        }

        return types;
    }

}
