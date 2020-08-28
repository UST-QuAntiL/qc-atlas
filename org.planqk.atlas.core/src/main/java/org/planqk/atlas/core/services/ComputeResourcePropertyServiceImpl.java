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

import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.model.ComputeResourceProperty;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.repository.AlgorithmRepository;
import org.planqk.atlas.core.repository.ComputeResourcePropertyRepository;
import org.planqk.atlas.core.repository.ComputeResourceRepository;
import org.planqk.atlas.core.repository.ImplementationRepository;
import org.planqk.atlas.core.util.ServiceUtils;
import org.planqk.atlas.core.util.ValidationUtils;

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
public class ComputeResourcePropertyServiceImpl implements ComputeResourcePropertyService {

    private final ComputeResourcePropertyRepository computeResourcePropertyRepository;
    private final ComputeResourcePropertyTypeService computeResourcePropertyTypeService;

    private final AlgorithmRepository algorithmRepository;
    private final ImplementationRepository implementationRepository;
    private final ComputeResourceRepository computeResourceRepository;

    @Override
    @Transactional
    public ComputeResourceProperty create(@NonNull ComputeResourceProperty computeResourceProperty) {
        computeResourceProperty.setComputeResourcePropertyType(
                computeResourcePropertyTypeService.findById(
                        computeResourceProperty.getComputeResourcePropertyType().getId()));

        return computeResourcePropertyRepository.save(computeResourceProperty);
    }

    @Override
    public ComputeResourceProperty findById(@NonNull UUID computeResourcePropertyId) {
        return ServiceUtils.findById(computeResourcePropertyId, ComputeResourceProperty.class,
                computeResourcePropertyRepository);
    }

    @Override
    @Transactional
    public ComputeResourceProperty update(@NonNull ComputeResourceProperty computeResourceProperty) {
        var computeResourcePropertyWithType = validateComputeResourceProperty(computeResourceProperty);

        var persistedComputeResourceProperty = findById(computeResourcePropertyWithType.getId());

        persistedComputeResourceProperty.setValue(computeResourcePropertyWithType.getValue());
        persistedComputeResourceProperty.setComputeResourcePropertyType(
                computeResourcePropertyWithType.getComputeResourcePropertyType());

        return computeResourcePropertyRepository.save(persistedComputeResourceProperty);
    }

    @Override
    @Transactional
    public void delete(@NonNull UUID computeResourcePropertyId) {
        ServiceUtils.throwIfNotExists(computeResourcePropertyId, ComputeResourceProperty.class,
                computeResourcePropertyRepository);

        computeResourcePropertyRepository.deleteById(computeResourcePropertyId);
    }

    @Override
    public Page<ComputeResourceProperty> findComputeResourcePropertiesOfAlgorithm(
            @NonNull UUID algorithmId, @NonNull Pageable pageable) {
        ServiceUtils.throwIfNotExists(algorithmId, Algorithm.class, algorithmRepository);
        return computeResourcePropertyRepository.findAllByAlgorithmId(algorithmId, pageable);
    }

    @Override
    public Page<ComputeResourceProperty> findComputeResourcePropertiesOfImplementation(
            @NonNull UUID implementationId, @NonNull Pageable pageable) {
        ServiceUtils.throwIfNotExists(implementationId, Implementation.class, implementationRepository);
        return computeResourcePropertyRepository.findAllByImplementationId(implementationId, pageable);
    }

    @Override
    public Page<ComputeResourceProperty> findComputeResourcePropertiesOfComputeResource(
            @NonNull UUID computeResourceId, @NonNull Pageable pageable) {
        ServiceUtils.throwIfNotExists(computeResourceId, ComputeResource.class, computeResourceRepository);
        return computeResourcePropertyRepository.findAllByComputeResourceId(computeResourceId, pageable);
    }

    @Override
    @Transactional
    public ComputeResourceProperty addComputeResourcePropertyToAlgorithm(
            @NonNull UUID algorithmId, @NonNull ComputeResourceProperty computeResourceProperty) {
        var computeResourcePropertyWithType = validateComputeResourceProperty(computeResourceProperty);

        ComputeResourceProperty persistedComputeResourceProperty;
        if (computeResourcePropertyWithType.getId() == null) {
            persistedComputeResourceProperty = this.create(computeResourcePropertyWithType);
        } else {
            persistedComputeResourceProperty = findById(computeResourcePropertyWithType.getId());
        }

        Algorithm algorithm = ServiceUtils.findById(algorithmId, Algorithm.class, algorithmRepository);

        persistedComputeResourceProperty.setAlgorithm(algorithm);
        return this.computeResourcePropertyRepository.save(persistedComputeResourceProperty);
    }

    @Override
    @Transactional
    public ComputeResourceProperty addComputeResourcePropertyToImplementation(
            @NonNull UUID implementationId, @NonNull ComputeResourceProperty computeResourceProperty) {
        var computeResourcePropertyWithType = validateComputeResourceProperty(computeResourceProperty);

        ComputeResourceProperty persistedComputeResourceProperty;
        if (computeResourcePropertyWithType.getId() == null) {
            persistedComputeResourceProperty = this.create(computeResourcePropertyWithType);
        } else {
            persistedComputeResourceProperty = findById(computeResourcePropertyWithType.getId());
        }

        Implementation implementation = ServiceUtils
                .findById(implementationId, Implementation.class, implementationRepository);

        persistedComputeResourceProperty.setImplementation(implementation);
        return this.computeResourcePropertyRepository.save(persistedComputeResourceProperty);
    }

    @Override
    @Transactional
    public ComputeResourceProperty addComputeResourcePropertyToComputeResource(
            @NonNull UUID computeResourceId, @NonNull ComputeResourceProperty computeResourceProperty) {
        var computeResourcePropertyWithType = validateComputeResourceProperty(computeResourceProperty);

        ComputeResourceProperty persistedComputeResourceProperty;
        if (computeResourcePropertyWithType.getId() == null) {
            persistedComputeResourceProperty = this.create(computeResourcePropertyWithType);
        } else {
            persistedComputeResourceProperty = findById(computeResourcePropertyWithType.getId());
        }

        ComputeResource computeResource = ServiceUtils
                .findById(computeResourceId, ComputeResource.class, computeResourceRepository);

        persistedComputeResourceProperty.setComputeResource(computeResource);
        return this.computeResourcePropertyRepository.save(persistedComputeResourceProperty);
    }

    private ComputeResourceProperty validateComputeResourceProperty(ComputeResourceProperty computeResourceProperty) {
        computeResourceProperty.setComputeResourcePropertyType(computeResourcePropertyTypeService
                .findById(computeResourceProperty.getComputeResourcePropertyType().getId()));

        ValidationUtils.validateComputeResourceProperty(computeResourceProperty);

        return computeResourceProperty;
    }
}
