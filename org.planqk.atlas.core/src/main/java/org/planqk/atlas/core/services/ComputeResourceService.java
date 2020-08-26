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

import org.planqk.atlas.core.model.CloudService;
import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.model.SoftwarePlatform;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface ComputeResourceService {

    Page<ComputeResource> searchAllByName(String name, Pageable pageable);

    @Transactional
    ComputeResource create(ComputeResource computeResource);

    Page<ComputeResource> findAll(Pageable pageable);

    ComputeResource findById(UUID computeResourceId);

    @Transactional
    ComputeResource update(ComputeResource computeResource);

    @Transactional
    void delete(UUID computeResourceId);

    Page<CloudService> findLinkedComputeResources(UUID computeResourceId, Pageable pageable);

    Page<SoftwarePlatform> findLinkedSoftwarePlatforms(UUID computeResourceId, Pageable pageable);
}
