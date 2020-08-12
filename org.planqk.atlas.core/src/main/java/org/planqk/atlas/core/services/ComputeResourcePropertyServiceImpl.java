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
import org.planqk.atlas.core.model.ComputeResourceProperty;
import org.planqk.atlas.core.model.ComputeResourcePropertyType;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.repository.AlgorithmRepository;
import org.planqk.atlas.core.repository.ComputeResourceRepository;
import org.planqk.atlas.core.repository.ComputeResourcePropertyRepository;
import org.planqk.atlas.core.repository.ComputeResourcePropertyTypeRepository;
import org.planqk.atlas.core.repository.ImplementationRepository;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ComputeResourcePropertyServiceImpl implements ComputeResourcePropertyService {

    private final AlgorithmRepository algorithmRepository;
    private final ComputeResourceRepository computeResourceRepository;
    private final ImplementationRepository implementationRepository;
    private final ComputeResourcePropertyTypeRepository typeRepository;
    private final ComputeResourcePropertyRepository resourceRepository;

    @Override
    @Transactional
    public void deleteComputeResourcePropertyType(UUID typeId) {
        // TODO throw consistency exception if object is still linked!
        this.typeRepository.deleteById(typeId);
    }

    @Override
    @Transactional
    public void deleteComputeResourceProperty(UUID resourceId) {
        resourceRepository.deleteById(resourceId);
    }

    @Override
    public ComputeResourcePropertyType findComputeResourcePropertyTypeById(UUID resourceTypeId) {
        return this.typeRepository.findById(resourceTypeId).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Page<ComputeResourcePropertyType> findAllComputeResourcePropertyTypes(Pageable pageable) {
        return typeRepository.findAll(pageable);
    }

    @Override
    public Set<ComputeResourceProperty> findAllComputeResourcesPropertyByAlgorithmId(UUID algoid) {
        return resourceRepository.findAllByAlgorithm_Id(algoid);
    }

    @Override
    public Page<ComputeResourceProperty> findAllComputeResourcesPropertyByAlgorithmId(UUID algoid, Pageable pageable) {
        return resourceRepository.findAllByAlgorithm_Id(algoid, pageable);
    }

    @Override
    public Set<ComputeResourceProperty> findAllComputeResourcesPropertiesByImplementationId(UUID implId) {
        return resourceRepository.findAllByImplementation_Id(implId);
    }

    @Override
    public Page<ComputeResourceProperty> findAllComputeResourcesPropertiesByImplementationId(UUID implId, Pageable pageable) {
        return resourceRepository.findAllByImplementation_Id(implId, pageable);
    }

    @Override
    public Set<ComputeResourceProperty> findAllComputeResourcesPropertiesByComputeResourceId(UUID backendId) {
        return resourceRepository.findAllByComputeResource_Id(backendId);
    }

    @Override
    public Page<ComputeResourceProperty> findAllComputeResourcesPropertiesByComputeResourceId(UUID backendId, Pageable pageable) {
        return resourceRepository.findAllByComputeResource_Id(backendId, pageable);
    }

    @Override
    @Transactional
    public ComputeResourcePropertyType saveComputeResourcePropertyType(ComputeResourcePropertyType resourceType) {
        return typeRepository.save(resourceType);
    }

    @Override
    @Transactional
    public ComputeResourceProperty saveComputeResourceProperty(ComputeResourceProperty resource) {
        if (resource.getComputeResourcePropertyType().getId() == null) {
            var type = saveComputeResourcePropertyType(resource.getComputeResourcePropertyType());
            resource.setComputeResourcePropertyType(type);
        }
        return resourceRepository.save(resource);
    }

    @Override
    @Transactional
    public ComputeResourceProperty addComputeResourcePropertyToAlgorithm(Algorithm algo, ComputeResourceProperty resource) {
        var updatedResource = resource;
        if (updatedResource.getId() == null) {
            updatedResource = this.saveComputeResourceProperty(resource);
        }
        updatedResource.setAlgorithm(algo);
        return this.resourceRepository.save(updatedResource);
    }

    @Override
    @Transactional
    public ComputeResourceProperty addComputeResourcePropertyToAlgorithm(UUID algoId, UUID resourceId) {
        var resource = resourceRepository.findById(resourceId).orElseThrow(NoSuchElementException::new);
        var algorithm = (Algorithm) algorithmRepository.findById(algoId).orElseThrow(NoSuchElementException::new);
        return addComputeResourcePropertyToAlgorithm(algorithm, resource);
    }

    @Override
    public ComputeResourceProperty addComputeResourcePropertyToImplementation(Implementation impl, ComputeResourceProperty resource) {
        var updatedResource = resource;
        if (updatedResource.getId() == null) {
            updatedResource = this.saveComputeResourceProperty(resource);
        }
        updatedResource.setImplementation(impl);
        return this.resourceRepository.save(updatedResource);
    }

    @Override
    public ComputeResourceProperty addComputeResourcePropertyToImplementation(UUID implId, UUID resourceId) {
        var resource = resourceRepository.findById(resourceId).orElseThrow(NoSuchElementException::new);
        var implementation = (Implementation) implementationRepository.findById(implId).orElseThrow(NoSuchElementException::new);
        return addComputeResourcePropertyToImplementation(implementation, resource);
    }

    @Override
    @Transactional
    public ComputeResourceProperty addComputeResourcePropertyToComputeResource(
            ComputeResource computeResource, ComputeResourceProperty computeResourceProperty) {
        computeResourceProperty.setComputeResource(computeResource);

        this.saveComputeResourceProperty(computeResourceProperty);

        return this.resourceRepository.save(computeResourceProperty);
    }

    @Override
    @Transactional
    public ComputeResourceProperty addComputeResourcePropertyToComputeResource(UUID computeResourceId, UUID propertyId) {
        var resource = resourceRepository.findById(propertyId).orElseThrow(NoSuchElementException::new);
        var computeResource = (ComputeResource) computeResourceRepository.findById(computeResourceId).orElseThrow(NoSuchElementException::new);
        return addComputeResourcePropertyToComputeResource(computeResource, resource);
    }

    @Override
    public ComputeResourceProperty findComputeResourcePropertyById(UUID id) {
        return resourceRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }
}
