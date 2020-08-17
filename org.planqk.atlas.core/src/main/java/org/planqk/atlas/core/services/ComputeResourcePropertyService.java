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
    void deleteComputeResourcePropertyType(UUID typeId);

    @Transactional
    void deleteComputeResourceProperty(UUID resourceId);

    ComputeResourcePropertyType findComputeResourcePropertyTypeById(UUID resourceTypeId);

    Page<ComputeResourcePropertyType> findAllComputeResourcePropertyTypes(Pageable pageable);

    Set<ComputeResourceProperty> findAllComputeResourcesPropertyByAlgorithmId(UUID algoid);

    Page<ComputeResourceProperty> findAllComputeResourcesPropertyByAlgorithmId(UUID algoid, Pageable pageable);

    Set<ComputeResourceProperty> findAllComputeResourcesPropertiesByImplementationId(UUID implId);

    Page<ComputeResourceProperty> findAllComputeResourcesPropertiesByImplementationId(UUID implId, Pageable pageable);

    Set<ComputeResourceProperty> findAllComputeResourcesPropertiesByComputeResourceId(UUID backendId);

    Page<ComputeResourceProperty> findAllComputeResourcesPropertiesByComputeResourceId(UUID backendId, Pageable pageable);

    @Transactional
    ComputeResourcePropertyType saveComputeResourcePropertyType(ComputeResourcePropertyType resourceType);

    @Transactional
    ComputeResourceProperty saveComputeResourceProperty(ComputeResourceProperty resource);

    @Transactional
    ComputeResourceProperty addComputeResourcePropertyToAlgorithm(Algorithm algo, ComputeResourceProperty resource);

    @Transactional
    ComputeResourceProperty addComputeResourcePropertyToAlgorithm(UUID algoId, UUID resourceId);

    @Transactional
    ComputeResourceProperty addComputeResourcePropertyToImplementation(Implementation implId, ComputeResourceProperty resource);

    @Transactional
    ComputeResourceProperty addComputeResourcePropertyToImplementation(UUID implId, UUID resourceId);

    @Transactional
    ComputeResourceProperty addComputeResourcePropertyToComputeResource(ComputeResource computeResource, ComputeResourceProperty resource);

    @Transactional
    ComputeResourceProperty addComputeResourcePropertyToComputeResource(UUID backend, UUID resourceId);

    ComputeResourceProperty findComputeResourcePropertyById(UUID id);
}
