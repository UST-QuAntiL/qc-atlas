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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ApplicationAreaServiceImpl implements ApplicationAreaService {

    private final ApplicationAreaRepository applicationAreaRepository;
    private final AlgorithmRepository algorithmRepository;

    @Override
    @Transactional
    public ApplicationArea save(ApplicationArea applicationArea) {
        return applicationAreaRepository.save(applicationArea);
    }

    @Override
    public Page<ApplicationArea> findAll(Pageable pageable, String search) {
        if (!Objects.isNull(search) && !search.isEmpty()) {
            return applicationAreaRepository.findAll(search, pageable);
        }
        return applicationAreaRepository.findAll(pageable);
    }

    @Override
    public ApplicationArea findById(UUID id) {
        return applicationAreaRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Override
    @Transactional
    public ApplicationArea update(UUID id, ApplicationArea applicationArea) {
        ApplicationArea persistedApplicationArea = findById(id);

        persistedApplicationArea.setName(applicationArea.getName());

        return save(persistedApplicationArea);
    }

    @Override
    @Transactional
    public void delete(UUID applicationAreaId) {
        ApplicationArea applicationArea = findById(applicationAreaId);

        if (applicationArea.getAlgorithms().size() > 0) {
            throw new ConsistencyException("Cannot delete application area with ID \"" + applicationAreaId +
                    "\". It is used by existing an algorithms!");
        }

        removeReferences(applicationArea);

        applicationAreaRepository.deleteById(applicationAreaId);
    }

    private void removeReferences(ApplicationArea applicationArea) {
        applicationArea.getAlgorithms().forEach(algorithm -> algorithm.removeApplicationArea(applicationArea));
    }
}
