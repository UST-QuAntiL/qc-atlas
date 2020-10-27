/*******************************************************************************
 * Copyright (c) 2020 the qc-atlas contributors.
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

import java.util.UUID;

import org.planqk.atlas.core.model.CloudService;
import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.model.SoftwarePlatform;
import org.planqk.atlas.core.repository.CloudServiceRepository;
import org.planqk.atlas.core.repository.ComputeResourceRepository;
import org.planqk.atlas.core.repository.SoftwarePlatformRepository;
import org.planqk.atlas.core.util.CollectionUtils;
import org.planqk.atlas.core.util.ServiceUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class CloudServiceServiceImpl implements CloudServiceService {

    private final CloudServiceRepository cloudServiceRepository;

    private final ComputeResourceRepository computeResourceRepository;

    private final SoftwarePlatformRepository softwarePlatformRepository;

    @Override
    public Page<CloudService> searchAllByName(String name, @NonNull Pageable pageable) {
        return cloudServiceRepository.findAllByNameContainingIgnoreCase(name, pageable);
    }

    @Override
    @Transactional
    public CloudService create(@NonNull CloudService cloudService) {
        return this.cloudServiceRepository.save(cloudService);
    }

    @Override
    public Page<CloudService> findAll(@NonNull Pageable pageable) {
        return cloudServiceRepository.findAll(pageable);
    }

    @Override
    public CloudService findById(@NonNull UUID cloudServiceId) {
        return ServiceUtils.findById(cloudServiceId, CloudService.class, cloudServiceRepository);
    }

    @Override
    @Transactional
    public CloudService update(@NonNull CloudService cloudService) {
        final CloudService persistedCloudService = findById(cloudService.getId());

        persistedCloudService.setName(cloudService.getName());
        persistedCloudService.setProvider(cloudService.getProvider());
        persistedCloudService.setUrl(cloudService.getUrl());
        persistedCloudService.setDescription(cloudService.getDescription());
        persistedCloudService.setCostModel(cloudService.getCostModel());

        return cloudServiceRepository.save(persistedCloudService);
    }

    @Override
    @Transactional
    public void delete(@NonNull UUID cloudServiceId) {
        final CloudService cloudService = findById(cloudServiceId);

        removeReferences(cloudService);

        cloudServiceRepository.deleteById(cloudServiceId);
    }

    private void removeReferences(@NonNull CloudService cloudService) {
        CollectionUtils.forEachOnCopy(cloudService.getSoftwarePlatforms(),
            softwarePlatform -> softwarePlatform.removeCloudService(cloudService));
        CollectionUtils.forEachOnCopy(cloudService.getProvidedComputeResources(),
            computeResource -> computeResource.removeCloudService(cloudService));
    }

    @Override
    public Page<ComputeResource> findLinkedComputeResources(@NonNull UUID cloudServiceId, @NonNull Pageable pageable) {
        ServiceUtils.throwIfNotExists(cloudServiceId, CloudService.class, cloudServiceRepository);
        return computeResourceRepository.findComputeResourcesByCloudServiceId(cloudServiceId, pageable);
    }

    @Override
    public Page<SoftwarePlatform> findLinkedSoftwarePlatforms(@NonNull UUID cloudServiceId, @NonNull Pageable pageable) {
        ServiceUtils.throwIfNotExists(cloudServiceId, CloudService.class, cloudServiceRepository);
        return softwarePlatformRepository.findSoftwarePlatformsByCloudServiceId(cloudServiceId, pageable);
    }
}
