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

import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.model.ComputingResourceProperty;
import org.planqk.atlas.core.model.ComputingResourcePropertyType;
import org.planqk.atlas.core.model.Implementation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ComputingResourcePropertyService {

    @Transactional
    void deleteComputingResourcePropertyType(UUID typeId);

    @Transactional
    void deleteComputingResourceProperty(UUID resourceId);

    ComputingResourcePropertyType findComputingResourcePropertyTypeById(UUID resourceTypeId);

    Page<ComputingResourcePropertyType> findAllComputingResourcePropertyTypes(Pageable pageable);

    Set<ComputingResourceProperty> findAllComputingResourcesPropertyByAlgorithmId(UUID algoid);

    Page<ComputingResourceProperty> findAllComputingResourcesPropertyByAlgorithmId(UUID algoid, Pageable pageable);

    Set<ComputingResourceProperty> findAllComputingResourcesPropertiesByImplementationId(UUID implId);

    Page<ComputingResourceProperty> findAllComputingResourcesPropertiesByImplementationId(UUID implId, Pageable pageable);

    Set<ComputingResourceProperty> findAllComputingResourcesPropertiesByComputeResourceId(UUID backendId);

    Page<ComputingResourceProperty> findAllComputingResourcesPropertiesByComputeResourceId(UUID backendId, Pageable pageable);

    @Transactional
    ComputingResourcePropertyType addOrUpdateComputingResourcePropertyType(ComputingResourcePropertyType resourceType);

    @Transactional
    ComputingResourceProperty addOrUpdateComputingResourceProperty(ComputingResourceProperty resource);

    @Transactional
    ComputingResourceProperty addComputingResourcePropertyToAlgorithm(Algorithm algo, ComputingResourceProperty resource);

    @Transactional
    ComputingResourceProperty addComputingResourcePropertyToAlgorithm(UUID algoId, UUID resourceId);

    @Transactional
    ComputingResourceProperty addComputingResourcePropertyToImplementation(Implementation implId, ComputingResourceProperty resource);

    @Transactional
    ComputingResourceProperty addComputingResourcePropertyToImplementation(UUID implId, UUID resourceId);

    @Transactional
    ComputingResourceProperty addComputingResourcePropertyToBackend(ComputeResource computeResource, ComputingResourceProperty resource);

    @Transactional
    ComputingResourceProperty addComputingResourcePropertyToBackend(UUID backend, UUID resourceId);

    ComputingResourceProperty findComputingResourcePropertyById(UUID id);
}
