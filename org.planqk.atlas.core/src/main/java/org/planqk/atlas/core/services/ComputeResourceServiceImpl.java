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
import java.util.Set;
import java.util.UUID;

import lombok.AllArgsConstructor;

import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.model.exceptions.ConsistencyException;
import org.planqk.atlas.core.repository.ComputeResourceRepository;
import org.planqk.atlas.core.repository.CloudServiceRepository;
import org.planqk.atlas.core.repository.SoftwarePlatformRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ComputeResourceServiceImpl implements ComputeResourceService {

    private final static Logger LOG = LoggerFactory.getLogger(ComputeResourceServiceImpl.class);

    private final ComputeResourceRepository repo;
    private final CloudServiceRepository cloudServiceRepository;
    private final SoftwarePlatformRepository softwarePlatformRepository;

    @Override
    @Transactional
    public ComputeResource save(ComputeResource computeResource) {
        return repo.save(computeResource);
    }

    @Override
    @Transactional
    public ComputeResource update(UUID id, ComputeResource computeResource) {
        ComputeResource persistedComputeResource = findById(id);

        // update fields that can be changed based on DTO
        persistedComputeResource.setName(computeResource.getName());
        persistedComputeResource.setVendor(computeResource.getVendor());
        persistedComputeResource.setTechnology(computeResource.getTechnology());
        persistedComputeResource.setQuantumComputationModel(computeResource.getQuantumComputationModel());

        return repo.save(persistedComputeResource);
    }

    @Override
    @Transactional
    public Set<ComputeResource> saveOrUpdateAll(Set<ComputeResource> computeResources) {
        for (ComputeResource computeResource : computeResources) {
            save(computeResource);
        }
        return computeResources;
    }

    @Override
    public ComputeResource findById(UUID id) {
        return repo.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Set<ComputeResource> findByName(String name) {
        return repo.findByName(name);
    }

    @Override
    public Page<ComputeResource> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!repo.existsById(id)) {
            throw new NoSuchElementException();
        }
        // TODO remove references
        // only delete if unused in SoftwarePlatforms and CloudServices
        long count = cloudServiceRepository.countCloudServiceByBackend(id) + softwarePlatformRepository.countSoftwarePlatformByComputeResource(id);
        if (count == 0) {
            repo.deleteById(id);
        } else {
            LOG.info("Trying to delete Backend that is used in a CloudService or SoftwarePlatform");
            throw new ConsistencyException(
                    "Cannot delete Backend since it is used by existing CloudService or SoftwarePlatform");
        }
    }
}
