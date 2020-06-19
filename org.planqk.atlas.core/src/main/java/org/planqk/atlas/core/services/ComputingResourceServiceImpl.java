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

import javax.transaction.Transactional;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.Backend;
import org.planqk.atlas.core.model.ComputingResource;
import org.planqk.atlas.core.model.ComputingResourceType;
import org.planqk.atlas.core.model.QuantumAlgorithm;
import org.planqk.atlas.core.repository.AlgorithmRepository;
import org.planqk.atlas.core.repository.BackendRepository;
import org.planqk.atlas.core.repository.ComputingResourceRepository;
import org.planqk.atlas.core.repository.ComputingResourceTypeRepository;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ComputingResourceServiceImpl implements ComputingResourceService {

    private final AlgorithmRepository algorithmRepository;
    private final BackendRepository backendRepository;
    private final ComputingResourceTypeRepository typeRepository;
    private final ComputingResourceRepository resourceRepository;

    @Override
    @Transactional
    public void deleteComputingResourceType(UUID typeId) {
        this.typeRepository.deleteById(typeId);
    }

    @Override
    @Transactional
    public void deleteComputingResource(UUID resourceId) {
        resourceRepository.deleteById(resourceId);
    }

    @Override
    public ComputingResourceType findResourceTypeById(UUID resourceTypeId) {
        return this.typeRepository.findById(resourceTypeId).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Page<ComputingResourceType> findAllResourceTypes(Pageable pageable) {
        return typeRepository.findAll(pageable);
    }

    @Override
    public Set<ComputingResource> findAllResourcesByAlgorithmId(UUID algoid) {
        return resourceRepository.findAllByAlgorithm_Id(algoid);
    }

    @Override
    public Page<ComputingResource> findAllResourcesByAlgorithmId(UUID algoid, Pageable pageable) {
        return resourceRepository.findAllByAlgorithm_Id(algoid, pageable);
    }

    @Override
    public Set<ComputingResource> findAllResourcesByBackendId(UUID backendId) {
        return resourceRepository.findAllByBackend_Id(backendId);
    }

    @Override
    public Page<ComputingResource> findAllResourcesByBackendId(UUID backendId, Pageable pageable) {
        return resourceRepository.findAllByBackend_Id(backendId, pageable);
    }

    @Override
    @Transactional
    public ComputingResourceType addOrUpdateComputingResourceType(ComputingResourceType resourceType) {
        return typeRepository.save(resourceType);
    }

    @Override
    @Transactional
    public ComputingResource addOrUpdateComputingResource(ComputingResource resource) {
        if (resource.getComputingResourceType().getId() == null) {
            var type = addOrUpdateComputingResourceType(resource.getComputingResourceType());
            resource.setComputingResourceType(type);
        }
        return resourceRepository.save(resource);
    }

    @Override
    @Transactional
    public ComputingResource addComputingResourceToAlgorithm(Algorithm algo, ComputingResource resource) {
        var updatedResource = resource;
        if (updatedResource.getId() == null) {
            updatedResource = this.addOrUpdateComputingResource(resource);
        }
        updatedResource.setAlgorithm(algo);
        return this.resourceRepository.save(updatedResource);
    }

    @Override
    @Transactional
    public ComputingResource addComputingResourceToAlgorithm(UUID algoId, UUID resourceId) {
        var resource = resourceRepository.findById(resourceId).orElseThrow(NoSuchElementException::new);
        var algorithm = (QuantumAlgorithm) algorithmRepository.findById(algoId).orElseThrow(NoSuchElementException::new);

        return addComputingResourceToAlgorithm(algorithm, resource);
    }

    @Override
    @Transactional
    public ComputingResource addComputingResourceToBackend(Backend backend, ComputingResource resource) {
        var updatedResource = resource;
        if (updatedResource.getId() == null) {
            updatedResource = this.addOrUpdateComputingResource(resource);
        }
        updatedResource.setBackend(backend);
        return this.resourceRepository.save(updatedResource);
    }

    @Override
    @Transactional
    public ComputingResource addComputingResourceToBackend(UUID backendId, UUID resourceId) {
        var resource = resourceRepository.findById(resourceId).orElseThrow(NoSuchElementException::new);
        var backend = (Backend) backendRepository.findById(backendId).orElseThrow(NoSuchElementException::new);
        return addComputingResourceToBackend(backend, resource);
    }

    @Override
    public ComputingResource findResourceById(UUID id) {
        return resourceRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }
}
