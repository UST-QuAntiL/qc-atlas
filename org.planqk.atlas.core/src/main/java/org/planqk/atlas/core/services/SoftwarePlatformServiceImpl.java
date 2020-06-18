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

import lombok.AllArgsConstructor;

import org.planqk.atlas.core.model.SoftwarePlatform;
import org.planqk.atlas.core.repository.SoftwarePlatformRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SoftwarePlatformServiceImpl implements SoftwarePlatformService {

    private final SoftwarePlatformRepository softwarePlatformRepository;
    private final CloudServiceService cloudServiceService;
    private final BackendService backendService;

    @Transactional
    @Override
    public SoftwarePlatform save(SoftwarePlatform softwarePlatform) {
        backendService.saveOrUpdateAll(softwarePlatform.getSupportedBackends());
        cloudServiceService.createOrUpdateAll(softwarePlatform.getSupportedCloudServices());

        return this.softwarePlatformRepository.save(softwarePlatform);
    }

    @Override
    public Page<SoftwarePlatform> findAll(Pageable pageable) {
        return softwarePlatformRepository.findAll(pageable);
    }

    @Override
    public SoftwarePlatform findById(UUID platformId) {
        return softwarePlatformRepository.findById(platformId).orElseThrow(NoSuchElementException::new);
    }

    @Transactional
    @Override
    public SoftwarePlatform update(UUID id, SoftwarePlatform softwarePlatform) {
        if (softwarePlatformRepository.existsSoftwarePlatformById(id)) {
            softwarePlatform.setId(id);
            return save(softwarePlatform);
        }
        throw new NoSuchElementException();
    }

    @Transactional
    @Override
    public void delete(UUID platformId) {
        softwarePlatformRepository.deleteById(platformId);
    }
}
