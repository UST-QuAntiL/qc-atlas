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

import org.planqk.atlas.core.model.CloudService;
import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.SoftwarePlatform;
import org.planqk.atlas.core.repository.CloudServiceRepository;
import org.planqk.atlas.core.repository.ComputeResourceRepository;
import org.planqk.atlas.core.repository.ImplementationRepository;
import org.planqk.atlas.core.repository.SoftwarePlatformRepository;
import org.planqk.atlas.core.util.ServiceUtils;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class SoftwarePlatformServiceImpl implements SoftwarePlatformService {

    private final SoftwarePlatformRepository softwarePlatformRepository;

    private final ImplementationRepository implementationRepository;

    private final ComputeResourceRepository computeResourceRepository;

    private final CloudServiceRepository cloudServiceRepository;

    @Override
    public Page<SoftwarePlatform> searchAllByName(String name, @NonNull Pageable pageable) {
        if (name == null) {
            name = "";
        }
        return softwarePlatformRepository.findAllByNameContainingIgnoreCase(name, pageable);
    }

    @Override
    @Transactional
    public SoftwarePlatform create(@NonNull SoftwarePlatform softwarePlatform) {
        return this.softwarePlatformRepository.save(softwarePlatform);
    }

    @Override
    public Page<SoftwarePlatform> findAll(@NonNull Pageable pageable) {
        return softwarePlatformRepository.findAll(pageable);
    }

    @Override
    public SoftwarePlatform findById(@NonNull UUID softwarePlatformId) {
        return ServiceUtils.findById(softwarePlatformId, SoftwarePlatform.class, softwarePlatformRepository);
    }

    @Override
    @Transactional
    public SoftwarePlatform update(@NonNull SoftwarePlatform softwarePlatform) {
        SoftwarePlatform persistedSoftwarePlatform = findById(softwarePlatform.getId());

        persistedSoftwarePlatform.setName(softwarePlatform.getName());
        persistedSoftwarePlatform.setLink(softwarePlatform.getLink());
        persistedSoftwarePlatform.setLicence(softwarePlatform.getLicence());
        persistedSoftwarePlatform.setVersion(softwarePlatform.getVersion());

        return softwarePlatformRepository.save(softwarePlatform);
    }

    @Override
    @Transactional
    public void delete(@NonNull UUID softwarePlatformId) {
        SoftwarePlatform softwarePlatform = findById(softwarePlatformId);

        removeReferences(softwarePlatform);

        softwarePlatformRepository.deleteById(softwarePlatformId);
    }

    private void removeReferences(@NonNull SoftwarePlatform softwarePlatform) {
        softwarePlatform.getImplementations().forEach(
                implementation -> implementation.removeSoftwarePlatform(softwarePlatform));
        softwarePlatform.getSupportedCloudServices().forEach(
                cloudService -> cloudService.removeSoftwarePlatform(softwarePlatform));
        softwarePlatform.getSupportedComputeResources().forEach(
                computeResource -> computeResource.removeSoftwarePlatform(softwarePlatform));
    }

    @Override
    public Page<Implementation> findLinkedImplementations(@NonNull UUID softwarePlatformId, @NonNull Pageable pageable) {
        ServiceUtils.throwIfNotExists(softwarePlatformId, SoftwarePlatform.class, softwarePlatformRepository);

        return implementationRepository.findImplementationsBySoftwarePlatformId(softwarePlatformId, pageable);
    }

    @Override
    public Page<CloudService> findLinkedCloudServices(@NonNull UUID softwarePlatformId, @NonNull Pageable pageable) {
        ServiceUtils.throwIfNotExists(softwarePlatformId, SoftwarePlatform.class, softwarePlatformRepository);

        return cloudServiceRepository.findCloudServicesBySoftwarePlatformId(softwarePlatformId, pageable);
    }

    @Override
    public Page<ComputeResource> findLinkedComputeResources(@NonNull UUID softwarePlatformId, @NonNull Pageable pageable) {
        ServiceUtils.throwIfNotExists(softwarePlatformId, SoftwarePlatform.class, softwarePlatformRepository);

        return computeResourceRepository.findComputeResourcesBySoftwarePlatformId(softwarePlatformId, pageable);
    }

    @Override
    public void checkIfImplementationIsLinkedToSoftwarePlatform(UUID softwarePlatformId, UUID implementationId) {
        ServiceUtils.throwIfNotExists(softwarePlatformId, SoftwarePlatform.class, softwarePlatformRepository);
        Implementation implementation = ServiceUtils.findById(implementationId, Implementation.class, implementationRepository);

        if (!ServiceUtils.containsElementWithId(implementation.getSoftwarePlatforms(), softwarePlatformId)) {
            throw new NoSuchElementException("Implementation with ID \"" + implementationId
                    + "\" is not linked to SoftwarePlatform with ID \"" + softwarePlatformId +  "\"");
        }
    }
}
