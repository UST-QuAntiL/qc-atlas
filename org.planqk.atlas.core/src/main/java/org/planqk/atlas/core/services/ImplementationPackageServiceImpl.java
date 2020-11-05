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

import org.planqk.atlas.core.model.ImplementationPackage;
import org.planqk.atlas.core.repository.ImplementationPackageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ImplementationPackageServiceImpl implements ImplementationPackageService {

    private final ImplementationPackageRepository implementationPackageRepository;

    @Override
    public ImplementationPackageRepository create(
            ImplementationPackageRepository implementationPackage) {
        return null;
    }

    @Override
    public ImplementationPackageRepository update(ImplementationPackageRepository implementationPackage) {
        return null;
    }

    @Override
    public void delete(UUID implementationPackageId) {

    }

    @Override
    public ImplementationPackage findById(UUID packageId) {
        return null;
    }

    @Override
    public Page<ImplementationPackage> findImplementationPackagesByImplementationId(UUID implementationId, @NonNull Pageable pageable) {
        return implementationPackageRepository.findImplementationPackagesByImplementationId(implementationId, pageable);
    }

    @Override
    public void checkIfImplementationPackageIsLinkedToImplemenation(UUID packageId, UUID implementationId) {
        final ImplementationPackage implementationPackage = findById(packageId);

        if (!implementationPackage.getImplementation().getId().equals(implementationId)) {
            throw new NoSuchElementException("ImplementationPackage with ID \"" + packageId
                    + "\" of Implementation with ID \"" + implementationId + "\" does not exist");
        }
    }
}
