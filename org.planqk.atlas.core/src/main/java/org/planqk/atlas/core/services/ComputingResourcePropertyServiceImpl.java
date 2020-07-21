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
import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.model.ComputingResourceProperty;
import org.planqk.atlas.core.model.ComputingResourcePropertyType;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.repository.AlgorithmRepository;
import org.planqk.atlas.core.repository.ComputeResourceRepository;
import org.planqk.atlas.core.repository.ComputingResourcePropertyRepository;
import org.planqk.atlas.core.repository.ComputingResourcePropertyTypeRepository;
import org.planqk.atlas.core.repository.ImplementationRepository;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ComputingResourcePropertyServiceImpl implements ComputingResourcePropertyService {

    private final AlgorithmRepository algorithmRepository;
    private final ComputeResourceRepository computeResourceRepository;
    private final ImplementationRepository implementationRepository;
    private final ComputingResourcePropertyTypeRepository typeRepository;
    private final ComputingResourcePropertyRepository resourceRepository;

    @Override
    @Transactional
    public void deleteComputingResourcePropertyType(UUID typeId) {
        this.typeRepository.deleteById(typeId);
    }

    @Override
    @Transactional
    public void deleteComputingResourceProperty(UUID resourceId) {
        resourceRepository.deleteById(resourceId);
    }

    @Override
    public ComputingResourcePropertyType findComputingResourcePropertyTypeById(UUID resourceTypeId) {
        return this.typeRepository.findById(resourceTypeId).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Page<ComputingResourcePropertyType> findAllComputingResourcePropertyTypes(Pageable pageable) {
        return typeRepository.findAll(pageable);
    }

    @Override
    public Set<ComputingResourceProperty> findAllComputingResourcesPropertyByAlgorithmId(UUID algoid) {
        return resourceRepository.findAllByAlgorithm_Id(algoid);
    }

    @Override
    public Page<ComputingResourceProperty> findAllComputingResourcesPropertyByAlgorithmId(UUID algoid, Pageable pageable) {
        return resourceRepository.findAllByAlgorithm_Id(algoid, pageable);
    }

    @Override
    public Set<ComputingResourceProperty> findAllComputingResourcesPropertiesByImplementationId(UUID implId) {
        return resourceRepository.findAllByImplementation_Id(implId);
    }

    @Override
    public Page<ComputingResourceProperty> findAllComputingResourcesPropertiesByImplementationId(UUID implId, Pageable pageable) {
        return resourceRepository.findAllByImplementation_Id(implId, pageable);
    }

    @Override
    public Set<ComputingResourceProperty> findAllComputingResourcesPropertiesByComputeResourceId(UUID backendId) {
        return resourceRepository.findAllByComputeResource_Id(backendId);
    }

    @Override
    public Page<ComputingResourceProperty> findAllComputingResourcesPropertiesByComputeResourceId(UUID backendId, Pageable pageable) {
        return resourceRepository.findAllByComputeResource_Id(backendId, pageable);
    }

    @Override
    @Transactional
    public ComputingResourcePropertyType addOrUpdateComputingResourcePropertyType(ComputingResourcePropertyType resourceType) {
        return typeRepository.save(resourceType);
    }

    @Override
    @Transactional
    public ComputingResourceProperty addOrUpdateComputingResourceProperty(ComputingResourceProperty resource) {
        if (resource.getComputingResourcePropertyType().getId() == null) {
            var type = addOrUpdateComputingResourcePropertyType(resource.getComputingResourcePropertyType());
            resource.setComputingResourcePropertyType(type);
        }
        return resourceRepository.save(resource);
    }

    @Override
    @Transactional
    public ComputingResourceProperty addComputingResourcePropertyToAlgorithm(Algorithm algo, ComputingResourceProperty resource) {
        var updatedResource = resource;
        if (updatedResource.getId() == null) {
            updatedResource = this.addOrUpdateComputingResourceProperty(resource);
        }
        updatedResource.setAlgorithm(algo);
        return this.resourceRepository.save(updatedResource);
    }

    @Override
    @Transactional
    public ComputingResourceProperty addComputingResourcePropertyToAlgorithm(UUID algoId, UUID resourceId) {
        var resource = resourceRepository.findById(resourceId).orElseThrow(NoSuchElementException::new);
        var algorithm = (Algorithm) algorithmRepository.findById(algoId).orElseThrow(NoSuchElementException::new);
        return addComputingResourcePropertyToAlgorithm(algorithm, resource);
    }

    @Override
    public ComputingResourceProperty addComputingResourcePropertyToImplementation(Implementation impl, ComputingResourceProperty resource) {
        var updatedResource = resource;
        if (updatedResource.getId() == null) {
            updatedResource = this.addOrUpdateComputingResourceProperty(resource);
        }
        updatedResource.setImplementation(impl);
        return this.resourceRepository.save(updatedResource);
    }

    @Override
    public ComputingResourceProperty addComputingResourcePropertyToImplementation(UUID implId, UUID resourceId) {
        var resource = resourceRepository.findById(resourceId).orElseThrow(NoSuchElementException::new);
        var implementation = (Implementation) implementationRepository.findById(implId).orElseThrow(NoSuchElementException::new);
        return addComputingResourcePropertyToImplementation(implementation, resource);
    }

    @Override
    @Transactional
    public ComputingResourceProperty addComputingResourcePropertyToBackend(ComputeResource computeResource, ComputingResourceProperty resource) {
        var updatedResource = resource;
        if (updatedResource.getId() == null) {
            updatedResource = this.addOrUpdateComputingResourceProperty(resource);
        }
        updatedResource.setComputeResource(computeResource);
        return this.resourceRepository.save(updatedResource);
    }

    @Override
    @Transactional
    public ComputingResourceProperty addComputingResourcePropertyToBackend(UUID backendId, UUID resourceId) {
        var resource = resourceRepository.findById(resourceId).orElseThrow(NoSuchElementException::new);
        var backend = (ComputeResource) computeResourceRepository.findById(backendId).orElseThrow(NoSuchElementException::new);
        return addComputingResourcePropertyToBackend(backend, resource);
    }

    @Override
    public ComputingResourceProperty findComputingResourcePropertyById(UUID id) {
        return resourceRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }
}
