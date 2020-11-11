/*******************************************************************************
 * Copyright (c) 2020 the qc-atlas contributors.
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

import java.util.NoSuchElementException;
import java.util.UUID;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;

import org.planqk.atlas.core.model.File;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.ImplementationPackage;
import org.planqk.atlas.core.repository.FileRepository;
import org.planqk.atlas.core.repository.ImplementationPackageRepository;
import org.planqk.atlas.core.repository.ImplementationRepository;
import org.planqk.atlas.core.util.ServiceUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ImplementationPackageServiceImpl implements ImplementationPackageService {

    private final ImplementationPackageRepository implementationPackageRepository;

    private final ImplementationRepository implementationRepository;

    private final FileRepository fileRepository;

    private final FileService fileService;

    @Override
    @Transactional
    public ImplementationPackage create(
            @NonNull ImplementationPackage implementationPackage, UUID implementationId) {
        final Implementation implementation = ServiceUtils.findById(implementationId, Implementation.class, implementationRepository);
        implementationPackage.setImplementation(implementation);
        return implementationPackageRepository.save(implementationPackage);
    }

    @Override
    @Transactional
    public ImplementationPackage update(@NotNull ImplementationPackage implementationPackage) {
        final ImplementationPackage persistedImplementationPackage = findById(implementationPackage.getId());

        persistedImplementationPackage.setName(implementationPackage.getName());
        persistedImplementationPackage.setDescription(implementationPackage.getDescription());
        persistedImplementationPackage.setPackageType(implementationPackage.getPackageType());
        return implementationPackageRepository.save(persistedImplementationPackage);
    }

    @Override
    @Transactional
    public void delete(UUID implementationPackageId) {
        // final ImplementationPackage implementationPackage = findById(implementationPackageId);

        // removeReferences(implementationPackage);

        implementationPackageRepository.deleteById(implementationPackageId);
    }

    @Override
    public ImplementationPackage findById(UUID packageId) {
        return ServiceUtils.findById(packageId, ImplementationPackage.class, implementationPackageRepository);
    }

    @Override
    public Page<ImplementationPackage> findImplementationPackagesByImplementationId(UUID implementationId, @NonNull Pageable pageable) {
        return implementationPackageRepository.findImplementationPackagesByImplementationId(implementationId, pageable);
    }

    @Override
    public void checkIfImplementationPackageIsLinkedToImplementation(UUID packageId, UUID implementationId) {
        final ImplementationPackage implementationPackage = findById(packageId);

        if (!implementationPackage.getImplementation().getId().equals(implementationId)) {
            throw new NoSuchElementException("ImplementationPackage with ID \"" + packageId
                    + "\" of Implementation with ID \"" + implementationId + "\" does not exist");
        }
    }

    @Override
    public File findLinkedFile(UUID implementationPackageId) {
        ServiceUtils.throwIfNotExists(implementationPackageId, ImplementationPackage.class, implementationPackageRepository);
        return fileRepository.findByImplementationPackage_Id(implementationPackageId)
                .orElseThrow(() -> new NoSuchElementException("ImplementationPackage with ID \"" + implementationPackageId + "\" does not exist"));
    }

    @Override
    public File addFileToImplementationPackage(UUID implementationPackageId, MultipartFile multipartFile) {
        final ImplementationPackage implementationPackage =
                ServiceUtils.findById(implementationPackageId, ImplementationPackage.class, implementationPackageRepository);
        final File file = fileService.create(implementationPackageId, multipartFile);
        implementationPackage.setFile(file);
        implementationPackageRepository.save(implementationPackage);
        return file;
    }
}
