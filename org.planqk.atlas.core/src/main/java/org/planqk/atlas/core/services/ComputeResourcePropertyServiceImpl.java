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

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.model.ComputeResourceProperty;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.repository.AlgorithmRepository;
import org.planqk.atlas.core.repository.ComputeResourcePropertyRepository;
import org.planqk.atlas.core.repository.ComputeResourceRepository;
import org.planqk.atlas.core.repository.ImplementationRepository;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ComputeResourcePropertyServiceImpl implements ComputeResourcePropertyService {

    private final ComputeResourcePropertyRepository computeResourcePropertyRepository;
    private final ComputeResourcePropertyTypeService computeResourcePropertyTypeService;

    private final AlgorithmRepository algorithmRepository;
    private final ImplementationRepository implementationRepository;
    private final ComputeResourceRepository computeResourceRepository;

    @Override
    @Transactional
    public ComputeResourceProperty create(ComputeResourceProperty computeResourceProperty) {
        computeResourceProperty.setComputeResourcePropertyType(
                computeResourcePropertyTypeService.findById(
                        computeResourceProperty.getComputeResourcePropertyType().getId()));

        return computeResourcePropertyRepository.save(computeResourceProperty);
    }

    @Override
    public ComputeResourceProperty findById(UUID computeResourcePropertyId) {
        return computeResourcePropertyRepository.findById(computeResourcePropertyId).orElseThrow(() -> {
            throw new NoSuchElementException("Cannot find ComputeResourceProperty with the given ID");
        });
    }

    @Override
    @Transactional
    public ComputeResourceProperty update(ComputeResourceProperty computeResourceProperty) {
        var persistedComputeResourceProperty = findById(computeResourceProperty.getId());

        persistedComputeResourceProperty.setValue(computeResourceProperty.getValue());
        persistedComputeResourceProperty.setComputeResourcePropertyType(computeResourceProperty.getComputeResourcePropertyType());

        return computeResourcePropertyRepository.save(persistedComputeResourceProperty);
    }

    @Override
    @Transactional
    public void delete(UUID computeResourcePropertyId) {
        if (!computeResourcePropertyRepository.existsById(computeResourcePropertyId)) {
            throw new NoSuchElementException("Element not found!");
        }
        computeResourcePropertyRepository.deleteById(computeResourcePropertyId);
    }

    @Override
    public Page<ComputeResourceProperty> findComputeResourcePropertiesOfAlgorithm(
            UUID algorithmId, Pageable pageable) {
        return computeResourcePropertyRepository.findAllByAlgorithm_Id(algorithmId, pageable);
    }

    @Override
    public Page<ComputeResourceProperty> findComputeResourcePropertiesOfImplementation(
            UUID implementationId, Pageable pageable) {
        return computeResourcePropertyRepository.findAllByImplementation_Id(implementationId, pageable);
    }

    @Override
    public Page<ComputeResourceProperty> findComputeResourcePropertiesOfComputeResource(
            UUID computeResourceId, Pageable pageable) {
        return computeResourcePropertyRepository.findAllByComputeResource_Id(computeResourceId, pageable);
    }

    @Override
    @Transactional
    public ComputeResourceProperty addComputeResourcePropertyToAlgorithm(
            UUID algorithmId, ComputeResourceProperty computeResourceProperty) {
        ComputeResourceProperty persistedComputeResourceProperty;
        if (computeResourceProperty.getId() == null) {
            persistedComputeResourceProperty = this.create(computeResourceProperty);
        } else {
            persistedComputeResourceProperty = findById(computeResourceProperty.getId());
        }

        Algorithm algorithm = algorithmRepository.findById(algorithmId)
                .orElseThrow(NoSuchElementException::new);

        persistedComputeResourceProperty.setAlgorithm(algorithm);
        return this.computeResourcePropertyRepository.save(persistedComputeResourceProperty);
    }

    @Override
    @Transactional
    public ComputeResourceProperty addComputeResourcePropertyToImplementation(
            UUID implementationId, ComputeResourceProperty computeResourceProperty) {
        ComputeResourceProperty persistedComputeResourceProperty;
        if (computeResourceProperty.getId() == null) {
            persistedComputeResourceProperty = this.create(computeResourceProperty);
        } else {
            persistedComputeResourceProperty = findById(computeResourceProperty.getId());
        }

        Implementation implementation = implementationRepository.findById(implementationId)
                .orElseThrow(NoSuchElementException::new);

        persistedComputeResourceProperty.setImplementation(implementation);
        return this.computeResourcePropertyRepository.save(persistedComputeResourceProperty);
    }

    @Override
    @Transactional
    public ComputeResourceProperty addComputeResourcePropertyToComputeResource(
            UUID computeResourceId, ComputeResourceProperty computeResourceProperty) {
        ComputeResourceProperty persistedComputeResourceProperty;
        if (computeResourceProperty.getId() == null) {
            persistedComputeResourceProperty = this.create(computeResourceProperty);
        } else {
            persistedComputeResourceProperty = findById(computeResourceProperty.getId());
        }

        ComputeResource computeResource = computeResourceRepository.findById(computeResourceId)
                .orElseThrow(NoSuchElementException::new);

        persistedComputeResourceProperty.setComputeResource(computeResource);
        return this.computeResourcePropertyRepository.save(persistedComputeResourceProperty);
    }
}
