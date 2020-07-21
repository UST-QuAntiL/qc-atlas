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

import lombok.AllArgsConstructor;

import org.planqk.atlas.core.model.CloudService;
import org.planqk.atlas.core.repository.CloudServiceRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class CloudServiceServiceImpl implements CloudServiceService {

    private final CloudServiceRepository cloudServiceRepository;

    @Override
    public CloudService save(CloudService cloudService) {
        return this.cloudServiceRepository.save(cloudService);
    }

    @Override
    public Set<CloudService> createOrUpdateAll(Set<CloudService> cloudServices) {
        return cloudServices.stream().map(this::save).collect(Collectors.toSet());
    }

    @Transactional
    @Override
    public CloudService update(UUID id, CloudService cloudService) {
        CloudService persistedCloudService = findById(id);

        // update fields that can be changed based on DTO
        persistedCloudService.setName(cloudService.getName());
        persistedCloudService.setProvider(cloudService.getProvider());
        persistedCloudService.setUrl(cloudService.getUrl());
        persistedCloudService.setDescription(cloudService.getDescription());
        persistedCloudService.setCostModel(cloudService.getCostModel());

        return save(persistedCloudService);
    }

    @Override
    public Page<CloudService> findAll(Pageable pageable) {
        return cloudServiceRepository.findAll(pageable);
    }

    @Override
    public CloudService findById(UUID cloudServiceId) {
        return cloudServiceRepository.findById(cloudServiceId).orElseThrow(NoSuchElementException::new);
    }

    @Transactional
    @Override
    public void delete(UUID cloudServiceId) {
        if (!cloudServiceRepository.existsById(cloudServiceId)) {
            throw new NoSuchElementException();
        }
        // TODO remove references
        cloudServiceRepository.deleteById(cloudServiceId);
    }
}
