/*******************************************************************************
 * Copyright (c) 2020-2021 the qc-atlas contributors.
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

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.planqk.atlas.core.WineryService;
import org.planqk.atlas.core.model.ToscaApplication;
import org.planqk.atlas.core.repository.ToscaApplicationRepository;
import org.planqk.atlas.core.util.ServiceUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@AllArgsConstructor
public class ToscaApplicationServiceImpl implements ToscaApplicationService {

    private final ToscaApplicationRepository toscaApplicationRepository;

    private final WineryService wineryService;

    @Override
    @Transactional
    public ToscaApplication create(ToscaApplication toscaApplication) {
        return this.toscaApplicationRepository.save(toscaApplication);
    }

    @Override
    public ToscaApplication createFromFile(MultipartFile file, String name) {
        log.info("Got file " + file.getOriginalFilename() + " of size " + file.getSize());
        final ToscaApplication toscaApplication = this.wineryService.uploadCsar(file.getResource(), name);
        return this.toscaApplicationRepository.save(toscaApplication);
    }

    @Override
    public Page<ToscaApplication> findAll(@NonNull Pageable pageable) {
        return this.toscaApplicationRepository.findAll(pageable);
    }

    @Override
    public ToscaApplication findById(@NonNull UUID toscaApplicationId) {
        return ServiceUtils.findById(toscaApplicationId, ToscaApplication.class, this.toscaApplicationRepository);
    }

    @Override
    public ToscaApplication update(@NonNull ToscaApplication toscaApplication) {
        final ToscaApplication persistedToscaApplication = findById(toscaApplication.getId());

        persistedToscaApplication.setName(toscaApplication.getName());

        return this.toscaApplicationRepository.save(persistedToscaApplication);
    }

    @Override
    public void delete(@NonNull UUID toscaApplicationId) {
        final ToscaApplication persistedToscaApplication = findById(toscaApplicationId);
        this.wineryService.delete(persistedToscaApplication);
        this.toscaApplicationRepository.deleteById(toscaApplicationId);
    }
}
