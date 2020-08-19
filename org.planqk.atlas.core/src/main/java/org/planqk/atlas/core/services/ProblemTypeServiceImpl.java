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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import javax.transaction.Transactional;

import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.model.exceptions.ConsistencyException;
import org.planqk.atlas.core.repository.AlgorithmRepository;
import org.planqk.atlas.core.repository.ProblemTypeRepository;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
    public void delete(ProblemType problemType) {
        if (this.algRepo.findAllByProblemTypesId(problemType.getId()).size() > 0) {
            LOG.info("Trying to delete ProblemType that is used by at least 1 algorithm");
            throw new ConsistencyException("Cannot delete ProbemType, since it is used by existing algorithms!");
        }
        repo.deleteById(problemType.getId());
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
    public List<ProblemType> getParentList(UUID id) {
        List<ProblemType> parentTree = new ArrayList<>();
        ProblemType requestedProblemType = findById(id);
        parentTree.add(requestedProblemType);
        ProblemType parentProblemType = getParent(requestedProblemType);
        while (parentProblemType != null) {
            parentTree.add(parentProblemType);
            parentProblemType = getParent(parentProblemType);
        }
        return parentTree;
    }

    // returns the parent problem type if present, else returns null
    private ProblemType getParent(ProblemType child) {
        try {
            if (child.getParentProblemType() != null) {
                return findById(child.getParentProblemType());
            }
        } catch (NoSuchElementException ignored) {
        }
        return null;
    }
}
