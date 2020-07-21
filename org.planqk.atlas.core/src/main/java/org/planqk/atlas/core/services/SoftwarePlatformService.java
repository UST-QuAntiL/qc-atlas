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

import org.planqk.atlas.core.model.CloudService;
import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.SoftwarePlatform;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface SoftwarePlatformService {

    @Transactional
    SoftwarePlatform save(SoftwarePlatform softwarePlatform);

    Page<SoftwarePlatform> findAll(Pageable pageable);

    SoftwarePlatform findById(UUID platformId);

    @Transactional
    SoftwarePlatform update(UUID id, SoftwarePlatform softwarePlatform);

    @Transactional
    void delete(UUID platformId);

    Page<Implementation> findImplementations(UUID platformId, Pageable pageable);

    @Transactional
    void addImplementationReference(UUID platformId, UUID implId);

    SoftwarePlatform getImplementation(UUID platformId, UUID implId);

    @Transactional
    void deleteImplementationReference(UUID platformId, UUID implId);

    Page<CloudService> findCloudServices(UUID platformId, Pageable pageable);

    @Transactional
    void addCloudServiceReference(UUID platformId, UUID cloudServiceId);

    @Transactional
    void deleteCloudServiceReference(UUID platformId, UUID cloudServiceId);

    Page<ComputeResource> findComputeResources(UUID platformId, Pageable pageable);

    @Transactional
    void addComputeResourceReference(UUID platformId, UUID resourceId);

    @Transactional
    void deleteComputeResourceReference(UUID platformId, UUID resourceId);
}
