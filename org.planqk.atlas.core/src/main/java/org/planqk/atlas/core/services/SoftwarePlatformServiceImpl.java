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
import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.SoftwarePlatform;
import org.planqk.atlas.core.model.exceptions.ConsistencyException;
import org.planqk.atlas.core.repository.CloudServiceRepository;
import org.planqk.atlas.core.repository.ComputeResourceRepository;
import org.planqk.atlas.core.repository.ImplementationRepository;
import org.planqk.atlas.core.repository.SoftwarePlatformRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SoftwarePlatformServiceImpl implements SoftwarePlatformService {

    private final SoftwarePlatformRepository softwarePlatformRepository;
    private final ImplementationRepository implementationRepository;
    private final ImplementationService implementationService;
    private final ComputeResourceRepository computeResourceRepository;
    private final ComputeResourceService computeResourceService;
    private final CloudServiceRepository cloudServiceRepository;
    private final CloudServiceService cloudServiceService;

    @Override
    @Transactional
    public SoftwarePlatform save(SoftwarePlatform softwarePlatform) {
        return this.softwarePlatformRepository.save(softwarePlatform);
    }

    @Override
    public Page<SoftwarePlatform> findAll(Pageable pageable) {
        return softwarePlatformRepository.findAll(pageable);
    }

    @Override
    public SoftwarePlatform findById(UUID platformId) {
        return softwarePlatformRepository.findById(platformId).orElseThrow(NoSuchElementException::new);
    }

    @Override
    @Transactional
    public SoftwarePlatform update(UUID platformId, SoftwarePlatform softwarePlatform) {
        SoftwarePlatform persistedSoftwarePlatform = findById(platformId);

        // update fields that can be changed based on DTO
        persistedSoftwarePlatform.setName(softwarePlatform.getName());
        persistedSoftwarePlatform.setLink(softwarePlatform.getLink());
        persistedSoftwarePlatform.setLicence(softwarePlatform.getLicence());
        persistedSoftwarePlatform.setVersion(softwarePlatform.getVersion());

        return save(softwarePlatform);
    }

    @Override
    @Transactional
    public void delete(UUID platformId) {
        if (!softwarePlatformRepository.existsSoftwarePlatformById(platformId)) {
            throw new NoSuchElementException();
        }
        // TODO remove references
        softwarePlatformRepository.deleteById(platformId);
    }

    @Override
    public Page<Implementation> findImplementations(UUID platformId, Pageable pageable) {
        if (!softwarePlatformRepository.existsSoftwarePlatformById(platformId)) {
            throw new NoSuchElementException();
        }
        return implementationRepository.findImplementationsBySoftwarePlatformId(platformId, pageable);
    }

    @Override
    @Transactional
    public void addImplementationReference(UUID platformId, UUID implId) {
        SoftwarePlatform softwarePlatform = findById(platformId);
        Implementation implementation = implementationService.findById(implId);

        if (softwarePlatform.getImplementations().contains(implementation)) {
            throw new ConsistencyException("Implementation and software platform are already linked");
        }

        softwarePlatform.getImplementations().add(implementation);
        save(softwarePlatform);
    }

    @Override
    public SoftwarePlatform getImplementation(UUID platformId, UUID implId) {
        return null;
    }

    @Override
    @Transactional
    public void deleteImplementationReference(UUID platformId, UUID implId) {
        SoftwarePlatform softwarePlatform = findById(platformId);
        Implementation implementation = implementationService.findById(implId);

        if (!softwarePlatform.getImplementations().contains(implementation)) {
            throw new ConsistencyException("Implementation and software platform are not linked");
        }

        softwarePlatform.getImplementations().remove(implementation);
        save(softwarePlatform);
    }

    @Override
    public Page<CloudService> findCloudServices(UUID platformId, Pageable pageable) {
        if (!softwarePlatformRepository.existsSoftwarePlatformById(platformId)) {
            throw new NoSuchElementException();
        }

        return null;
    }

    @Override
    @Transactional
    public void addCloudServiceReference(UUID platformId, UUID cloudServiceId) {
        SoftwarePlatform softwarePlatform = findById(platformId);
        CloudService cloudService = cloudServiceService.findById(cloudServiceId);

        if (!softwarePlatformRepository.existsSoftwarePlatformById(platformId)) {
            throw new ConsistencyException("Cloud service and software platform are already linked");
        }

        softwarePlatform.getSupportedCloudServices().add(cloudService);
        save(softwarePlatform);
    }

    @Override
    @Transactional
    public void deleteCloudServiceReference(UUID platformId, UUID cloudServiceId) {
        SoftwarePlatform softwarePlatform = findById(platformId);
        CloudService cloudService = cloudServiceService.findById(cloudServiceId);

        if (!softwarePlatformRepository.existsSoftwarePlatformById(platformId)) {
            throw new ConsistencyException("Cloud service and software platform are not linked");
        }

        softwarePlatform.getSupportedCloudServices().remove(cloudService);
        save(softwarePlatform);
    }

    @Override
    public Page<ComputeResource> findComputeResources(UUID platformId, Pageable pageable) {
        if (!softwarePlatformRepository.existsSoftwarePlatformById(platformId)) {
            throw new NoSuchElementException();
        }

        return computeResourceRepository.findComputeResourceBySoftwarePlatformId(platformId, pageable);
    }

    @Override
    @Transactional
    public void addComputeResourceReference(UUID platformId, UUID resourceId) {
        SoftwarePlatform softwarePlatform = findById(platformId);
        ComputeResource computeResource = computeResourceService.findById(resourceId);

        if (!softwarePlatformRepository.existsSoftwarePlatformById(platformId)) {
            throw new ConsistencyException("Compute resource and software platform are already linked");
        }

        softwarePlatform.getSupportedComputeResources().add(computeResource);
        save(softwarePlatform);
    }

    @Override
    @Transactional
    public void deleteComputeResourceReference(UUID platformId, UUID resourceId) {
        SoftwarePlatform softwarePlatform = findById(platformId);
        ComputeResource computeResource = computeResourceService.findById(resourceId);

        if (!softwarePlatformRepository.existsSoftwarePlatformById(platformId)) {
            throw new ConsistencyException("Compute resource and software platform are not linked");
        }

        softwarePlatform.getSupportedComputeResources().remove(computeResource);
        save(softwarePlatform);
    }
}
