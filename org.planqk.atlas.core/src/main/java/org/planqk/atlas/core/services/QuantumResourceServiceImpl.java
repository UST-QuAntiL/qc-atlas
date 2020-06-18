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

import org.planqk.atlas.core.model.ComputingResource;
import org.planqk.atlas.core.model.ComputingResourceType;
import org.planqk.atlas.core.model.QuantumAlgorithm;
import org.planqk.atlas.core.repository.AlgorithmRepository;
import org.planqk.atlas.core.repository.QuantumResourceRepository;
import org.planqk.atlas.core.repository.QuantumResourceTypeRepository;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class QuantumResourceServiceImpl implements QuantumResourceService {

    private final AlgorithmRepository algorithmRepository;
    private final QuantumResourceTypeRepository typeRepository;
    private final QuantumResourceRepository resourceRepository;

    @Override
    @Transactional
    public void deleteQuantumResourceType(UUID typeId) {
        this.typeRepository.deleteById(typeId);
    }

    @Override
    @Transactional
    public void deleteQuantumResource(UUID resourceId) {
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
    @Transactional
    public ComputingResourceType addOrUpdateQuantumResourceType(ComputingResourceType resourceType) {
        return typeRepository.save(resourceType);
    }

    @Override
    @Transactional
    public ComputingResource addOrUpdateQuantumResource(ComputingResource resource) {
        if (resource.getComputingResourceType().getId() == null) {
            var type = addOrUpdateQuantumResourceType(resource.getComputingResourceType());
            resource.setComputingResourceType(type);
        }
        return resourceRepository.save(resource);
    }

    @Override
    @Transactional
    public ComputingResource addQuantumResourceToAlgorithm(QuantumAlgorithm algo, ComputingResource resource) {
        var updatedResource = resource;
        if (updatedResource.getId() == null) {
            updatedResource = this.addOrUpdateQuantumResource(resource);
        }
        updatedResource.setAlgorithm(algo);
        return this.resourceRepository.save(updatedResource);
    }

    @Override
    @Transactional
    public ComputingResource addQuantumResourceToAlgorithm(UUID algoId, UUID resourceId) {
        var resource = resourceRepository.findById(resourceId).orElseThrow(NoSuchElementException::new);
        var algorithm = (QuantumAlgorithm) algorithmRepository.findById(algoId).orElseThrow(NoSuchElementException::new);

        return addQuantumResourceToAlgorithm(algorithm, resource);
    }

    @Override
    public ComputingResource findResourceById(UUID id) {
        return resourceRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }
}
