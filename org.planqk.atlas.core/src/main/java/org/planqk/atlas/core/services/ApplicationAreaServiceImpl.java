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

import java.util.Objects;
import java.util.UUID;

import javax.transaction.Transactional;

import org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException;
import org.planqk.atlas.core.model.ApplicationArea;
import org.planqk.atlas.core.repository.ApplicationAreaRepository;
import org.planqk.atlas.core.util.ServiceUtils;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ApplicationAreaServiceImpl implements ApplicationAreaService {

    private final ApplicationAreaRepository applicationAreaRepository;

    @Override
    @Transactional
    public ApplicationArea create(@NonNull ApplicationArea applicationArea) {
        return applicationAreaRepository.save(applicationArea);
    }

    @Override
    public Page<ApplicationArea> findAll(@NonNull Pageable pageable, String search) {
        if (!Objects.isNull(search) && !search.isEmpty()) {
            return applicationAreaRepository.findAll(search, pageable);
        }
        return applicationAreaRepository.findAll(pageable);
    }

    @Override
    public ApplicationArea findById(@NonNull UUID applicationAreaId) {
        return ServiceUtils.findById(applicationAreaId, ApplicationArea.class, applicationAreaRepository);
    }

    @Override
    @Transactional
    public ApplicationArea update(@NonNull ApplicationArea applicationArea) {
        ApplicationArea persistedApplicationArea = findById(applicationArea.getId());

        persistedApplicationArea.setName(applicationArea.getName());

        return applicationAreaRepository.save(persistedApplicationArea);
    }

    @Override
    @Transactional
    public void delete(@NonNull UUID applicationAreaId) {
        ApplicationArea applicationArea = findById(applicationAreaId);

        if (applicationArea.getAlgorithms().size() > 0) {
            throw new EntityReferenceConstraintViolationException("ApplicationArea with ID \""
                    + applicationAreaId + "\" cannot be deleted, because it is still in use");
        }

        // removeReferences(applicationArea);

        applicationAreaRepository.deleteById(applicationAreaId);
    }

    private void removeReferences(ApplicationArea applicationArea) {
        applicationArea.getAlgorithms().forEach(algorithm -> algorithm.removeApplicationArea(applicationArea));
    }
}
