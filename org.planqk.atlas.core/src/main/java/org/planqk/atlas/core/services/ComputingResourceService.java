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
import org.planqk.atlas.core.model.Backend;
import org.planqk.atlas.core.model.ComputingResource;
import org.planqk.atlas.core.model.ComputingResourceType;
import org.planqk.atlas.core.model.Implementation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ComputingResourceService {

    @Transactional
    void deleteComputingResourceType(UUID typeId);

    @Transactional
    void deleteComputingResource(UUID resourceId);

    ComputingResourceType findResourceTypeById(UUID resourceTypeId);

    Page<ComputingResourceType> findAllResourceTypes(Pageable pageable);

    Set<ComputingResource> findAllResourcesByAlgorithmId(UUID algoid);

    Page<ComputingResource> findAllResourcesByAlgorithmId(UUID algoid, Pageable pageable);

    Set<ComputingResource> findAllResourcesByImplementationId(UUID implId);

    Page<ComputingResource> findAllResourcesByImplementationId(UUID implId, Pageable pageable);

    Set<ComputingResource> findAllResourcesByBackendId(UUID backendId);

    Page<ComputingResource> findAllResourcesByBackendId(UUID backendId, Pageable pageable);

    @Transactional
    ComputingResourceType addOrUpdateComputingResourceType(ComputingResourceType resourceType);

    @Transactional
    ComputingResource addOrUpdateComputingResource(ComputingResource resource);

    @Transactional
    ComputingResource addComputingResourceToAlgorithm(Algorithm algo, ComputingResource resource);

    @Transactional
    ComputingResource addComputingResourceToAlgorithm(UUID algoId, UUID resourceId);

    @Transactional
    ComputingResource addComputingResourceToImplementation(Implementation implId, ComputingResource resource);

    @Transactional
    ComputingResource addComputingResourceToImplementation(UUID implId, UUID resourceId);

    @Transactional
    ComputingResource addComputingResourceToBackend(Backend backend, ComputingResource resource);

    @Transactional
    ComputingResource addComputingResourceToBackend(UUID backend, UUID resourceId);

    ComputingResource findResourceById(UUID id);
}
