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

import org.springframework.transaction.annotation.Transactional;

public interface LinkingService {

    @Transactional
    void linkAlgorithmAndPublication(UUID algorithmId, UUID publicationId);

    @Transactional
    void unlinkAlgorithmAndPublication(UUID algorithmId, UUID publicationId);

    @Transactional
    void linkAlgorithmAndProblemType(UUID algorithmId, UUID problemTypeId);

    @Transactional
    void unlinkAlgorithmAndProblemType(UUID algorithmId, UUID problemTypeId);

    @Transactional
    void linkAlgorithmAndApplicationArea(UUID algorithmId, UUID applicationAreaId);

    @Transactional
    void unlinkAlgorithmAndApplicationArea(UUID algorithmId, UUID applicationAreaId);

    @Transactional
    void linkImplementationAndPublication(UUID implementationId, UUID publicationId);

    @Transactional
    void unlinkImplementationAndPublication(UUID implementationId, UUID publicationId);

    @Transactional
    void linkImplementationAndSoftwarePlatform(UUID implementationId, UUID softwarePlatformId);

    @Transactional
    void unlinkImplementationAndSoftwarePlatform(UUID implementationId, UUID softwarePlatformId);

    @Transactional
    void linkSoftwarePlatformAndCloudService(UUID softwarePlatformId, UUID cloudServiceId);

    @Transactional
    void unlinkSoftwarePlatformAndCloudService(UUID softwarePlatformId, UUID cloudServiceId);

    @Transactional
    void linkSoftwarePlatformAndComputeResource(UUID softwarePlatformId, UUID computeResourceId);

    @Transactional
    void unlinkSoftwarePlatformAndComputeResource(UUID softwarePlatformId, UUID computeResourceId);

    @Transactional
    void linkCloudServiceAndComputeResource(UUID cloudServiceId, UUID computeResourceId);

    @Transactional
    void unlinkCloudServiceAndComputeResource(UUID cloudServiceId, UUID computeResourceId);
}
