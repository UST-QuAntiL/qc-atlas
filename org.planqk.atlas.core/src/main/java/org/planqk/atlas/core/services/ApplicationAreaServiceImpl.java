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
import java.util.Objects;
import java.util.UUID;

import javax.transaction.Transactional;

import org.planqk.atlas.core.model.ApplicationArea;
import org.planqk.atlas.core.model.exceptions.ConsistencyException;
import org.planqk.atlas.core.repository.AlgorithmRepository;
import org.planqk.atlas.core.repository.ApplicationAreaRepository;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ApplicationAreaServiceImpl implements ApplicationAreaService {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationAreaServiceImpl.class);

    private final ApplicationAreaRepository repo;
    private final AlgorithmRepository algRepo;

    @Override
    @Transactional
    public ApplicationArea save(ApplicationArea applicationArea) {
        return repo.save(applicationArea);
    }

    @Override
    @Transactional
    public ApplicationArea update(UUID id, ApplicationArea problemType) {
        // Get existing ApplicationArea if it exists
        ApplicationArea persistedType = findById(id);
        // Update fields
        persistedType.setName(problemType.getName());
        return save(persistedType);
    }

    @Override
    @Transactional
    public void delete(ApplicationArea applicationArea) {
        if (algRepo.findAllByApplicationAreas(applicationArea).size() > 0) {
            LOG.info("Trying to delete applicationArea that is used by at least 1 algorithm");
            throw new ConsistencyException("Cannot delete applicationArea, since it is used by existing algorithms!");
        }

        repo.delete(applicationArea);
    }

    @Override
    public ApplicationArea findById(UUID id) {
        return repo.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public ApplicationArea findByName(String name) {
        return repo.findByName(name).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Page<ApplicationArea> findAll(Pageable pageable, String search) {
        if (!Objects.isNull(search) && !search.isEmpty()) {
            return repo.findAll(search, pageable);
        }
        return repo.findAll(pageable);
    }
}
