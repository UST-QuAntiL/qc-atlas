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

import org.planqk.atlas.core.model.QuantumAlgorithm;
import org.planqk.atlas.core.model.QuantumResource;
import org.planqk.atlas.core.model.QuantumResourceType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuantumResourceService {
    @Transactional
    void deleteQuantumResourceType(UUID typeId);

    @Transactional
    void deleteQuantumResource(UUID resourceId);

    QuantumResourceType findResourceTypeById(UUID resourceTypeId);

    Page<QuantumResourceType> findAllResourceTypes(Pageable pageable);

    Set<QuantumResource> findAllResourcesByAlgorithmId(UUID algoid);

    @Transactional
    QuantumResourceType addOrUpdateQuantumResourceType(QuantumResourceType resourceType);

    @Transactional
    QuantumResource addOrUpdateQuantumResource(QuantumResource resource);

    @Transactional
    QuantumResource addQuantumResourceToAlgorithm(QuantumAlgorithm algo, QuantumResource resource);

    @Transactional
    QuantumResource addQuantumResourceToAlgorithm(UUID algoId, UUID resourceId);

    QuantumResource findResourceById(UUID id);
}
