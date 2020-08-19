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

import org.planqk.atlas.core.model.CloudService;
import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.model.SoftwarePlatform;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface CloudServiceService {

    Page<CloudService> searchAllByName(String name, Pageable p);

    @Transactional
    CloudService save(CloudService cloudService);

    @Transactional
    Set<CloudService> createOrUpdateAll(Set<CloudService> cloudServices);

    @Transactional
    CloudService update(UUID id, CloudService cloudService);

    Page<CloudService> findAll(Pageable pageable);

    CloudService findById(UUID cloudServiceId);

    @Transactional
    void delete(UUID cloudServiceId);

    Page<ComputeResource> findComputeResources(UUID serviceId, Pageable pageable);

    Page<SoftwarePlatform> findLinkedSoftwarePlatforms(UUID serviceId, Pageable pageable);
}
