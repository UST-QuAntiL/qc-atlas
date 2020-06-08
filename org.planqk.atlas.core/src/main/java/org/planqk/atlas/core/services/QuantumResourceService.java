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
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.planqk.atlas.core.model.QuantumAlgorithm;
import org.planqk.atlas.core.model.QuantumResource;
import org.planqk.atlas.core.model.QuantumResourceType;
import org.planqk.atlas.core.repository.AlgorithmRepository;
import org.planqk.atlas.core.repository.QuantumResourceRepository;
import org.planqk.atlas.core.repository.QuantumResourceTypeRepository;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class QuantumResourceService {

    private final AlgorithmService algorithmService;
    private final AlgorithmRepository algorithmRepository;
    private final QuantumResourceTypeRepository typeRepository;
    private final QuantumResourceRepository resourceRepository;

    @Transactional
    public void deleteQuantumResource(UUID resourceId) {
        resourceRepository.deleteById(resourceId);
    }

    public Page<QuantumResourceType> findAllResourceTypes(Pageable pageable) {
        return typeRepository.findAll(pageable);
    }

    @Transactional
    public QuantumResourceType addOrUpdateQuantumResourceType(QuantumResourceType resourceType) {
        return typeRepository.save(resourceType);
    }

    @Transactional
    public QuantumResource addOrUpdateQuantumResource(QuantumResource resource) {
        if (resource.getQuantumResourceType().getId() == null) {
            var type = addOrUpdateQuantumResourceType(resource.getQuantumResourceType());
            resource.setQuantumResourceType(type);
        }
        return resourceRepository.save(resource);
    }

    @Transactional
    public QuantumResource addQuantumResourceToAlgorithm(QuantumAlgorithm algo, QuantumResource resource) {
        var updatedResource = resource;
        if (updatedResource.getId() == null) {
            updatedResource = this.addOrUpdateQuantumResource(resource);
        }
        updatedResource.setAlgorithm(algo);
        return this.resourceRepository.save(updatedResource);
    }

    @Transactional
    public QuantumResource addQuantumResourceToAlgorithm(UUID algoId, UUID resourceId) {
        var resource = resourceRepository.findById(resourceId).orElseThrow(NoSuchElementException::new);
        var algorithm = (QuantumAlgorithm) algorithmRepository.findById(algoId).orElseThrow(NoSuchElementException::new);

        return addQuantumResourceToAlgorithm(algorithm, resource);
    }
}
