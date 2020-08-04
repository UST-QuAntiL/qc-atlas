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
import org.planqk.atlas.core.model.ComputeResourceProperty;
import org.planqk.atlas.core.model.ComputeResourcePropertyType;
import org.planqk.atlas.core.model.Implementation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ComputeResourcePropertyService {

    @Transactional
    void deleteComputingResourcePropertyType(UUID typeId);

    @Transactional
    void deleteComputingResourceProperty(UUID resourceId);

    ComputeResourcePropertyType findComputingResourcePropertyTypeById(UUID resourceTypeId);

    Page<ComputeResourcePropertyType> findAllComputingResourcePropertyTypes(Pageable pageable);

    Set<ComputeResourceProperty> findAllComputingResourcesPropertyByAlgorithmId(UUID algoid);

    Page<ComputeResourceProperty> findAllComputingResourcesPropertyByAlgorithmId(UUID algoid, Pageable pageable);

    Set<ComputeResourceProperty> findAllComputingResourcesPropertiesByImplementationId(UUID implId);

    Page<ComputeResourceProperty> findAllComputingResourcesPropertiesByImplementationId(UUID implId, Pageable pageable);

    Set<ComputeResourceProperty> findAllComputingResourcesPropertiesByComputeResourceId(UUID backendId);

    Page<ComputeResourceProperty> findAllComputingResourcesPropertiesByComputeResourceId(UUID backendId, Pageable pageable);

    @Transactional
    ComputeResourcePropertyType saveComputingResourcePropertyType(ComputeResourcePropertyType resourceType);

    @Transactional
    ComputeResourceProperty saveComputingResourceProperty(ComputeResourceProperty resource);

    @Transactional
    ComputeResourceProperty addComputingResourcePropertyToAlgorithm(Algorithm algo, ComputeResourceProperty resource);

    @Transactional
    ComputeResourceProperty addComputingResourcePropertyToAlgorithm(UUID algoId, UUID resourceId);

    @Transactional
    ComputeResourceProperty addComputingResourcePropertyToImplementation(Implementation implId, ComputeResourceProperty resource);

    @Transactional
    ComputeResourceProperty addComputingResourcePropertyToImplementation(UUID implId, UUID resourceId);

    @Transactional
    ComputeResourceProperty addComputingResourcePropertyToComputeResource(ComputeResource computeResource, ComputeResourceProperty resource);

    @Transactional
    ComputeResourceProperty addComputingResourcePropertyToComputeResource(UUID backend, UUID resourceId);

    ComputeResourceProperty findComputingResourcePropertyById(UUID id);
}
