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

import javax.transaction.Transactional;

import org.planqk.atlas.core.model.ComputeResourceProperty;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service class for operations related to interacting and modifying {@link ComputeResourceProperty}s in the database.
 */
public interface ComputeResourcePropertyService {

    @Transactional
    ComputeResourceProperty create(ComputeResourceProperty computeResourceProperty);

    ComputeResourceProperty findById(UUID computeResourcePropertyId);

    @Transactional
    ComputeResourceProperty update(ComputeResourceProperty computeResourceProperty);

    @Transactional
    void delete(UUID computeResourcePropertyId);

    Page<ComputeResourceProperty> findComputeResourcePropertiesOfAlgorithm(UUID algorithmId, Pageable pageable);

    Page<ComputeResourceProperty> findComputeResourcePropertiesOfImplementation(UUID implementationId, Pageable pageable);

    Page<ComputeResourceProperty> findComputeResourcePropertiesOfComputeResource(UUID computeResourceId, Pageable pageable);

    @Transactional
    ComputeResourceProperty addComputeResourcePropertyToAlgorithm(
            UUID algorithmId, ComputeResourceProperty computeResourceProperty);

    @Transactional
    ComputeResourceProperty addComputeResourcePropertyToImplementation(
            UUID implementationId, ComputeResourceProperty computeResourceProperty);

    @Transactional
    ComputeResourceProperty addComputeResourcePropertyToComputeResource(
            UUID computeResourceId, ComputeResourceProperty computeResourceProperty);

    void checkIfComputeResourcePropertyIsOfAlgorithm(UUID algorithmId, UUID computeResourcePropertyId);

    void checkIfComputeResourcePropertyIsOfImplementation(UUID implementationId, UUID computeResourcePropertyId);

    void checkIfComputeResourcePropertyIsOfComputeResource(UUID computeResourceId, UUID computeResourcePropertyId);

}
