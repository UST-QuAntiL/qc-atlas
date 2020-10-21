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

import java.util.UUID;

import org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException;
import org.planqk.atlas.core.model.ComputeResourcePropertyType;
import org.planqk.atlas.core.repository.ComputeResourcePropertyRepository;
import org.planqk.atlas.core.repository.ComputeResourcePropertyTypeRepository;
import org.planqk.atlas.core.util.ServiceUtils;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class ComputeResourcePropertyTypeServiceImpl implements ComputeResourcePropertyTypeService {

    private final ComputeResourcePropertyTypeRepository computeResourcePropertyTypeRepository;
    private final ComputeResourcePropertyRepository computeResourcePropertyRepository;

    @Override
    @Transactional
    public ComputeResourcePropertyType create(@NonNull ComputeResourcePropertyType computeResourcePropertyType) {
        return computeResourcePropertyTypeRepository.save(computeResourcePropertyType);
    }

    @Override
    public Page<ComputeResourcePropertyType> findAll(@NonNull Pageable pageable) {
        return computeResourcePropertyTypeRepository.findAll(pageable);
    }

    @Override
    public ComputeResourcePropertyType findById(@NonNull UUID computeResourcePropertyTypeId) {
        return ServiceUtils.findById(
                computeResourcePropertyTypeId,
                ComputeResourcePropertyType.class,
                computeResourcePropertyTypeRepository);
    }

    @Override
    @Transactional
    public ComputeResourcePropertyType update(@NonNull ComputeResourcePropertyType computeResourcePropertyType) {
        var persistedComputeResourcePropertyType = findById(computeResourcePropertyType.getId());

        persistedComputeResourcePropertyType.setName(computeResourcePropertyType.getName());
        persistedComputeResourcePropertyType.setDescription(computeResourcePropertyType.getDescription());
        persistedComputeResourcePropertyType.setDatatype(computeResourcePropertyType.getDatatype());

        return computeResourcePropertyTypeRepository.save(persistedComputeResourcePropertyType);
    }

    @Override
    @Transactional
    public void delete(@NonNull UUID computeResourcePropertyTypeId) {
        ServiceUtils.throwIfNotExists(
                computeResourcePropertyTypeId,
                ComputeResourcePropertyType.class,
                computeResourcePropertyTypeRepository);

        if (computeResourcePropertyRepository.countByComputeResourcePropertyTypeId(computeResourcePropertyTypeId) > 0) {
            throw new EntityReferenceConstraintViolationException("ComputeResourcePropertyType with ID \""
                    + computeResourcePropertyTypeId + "\" cannot be deleted, because it is still in use");
        }

        this.computeResourcePropertyTypeRepository.deleteById(computeResourcePropertyTypeId);
    }
}
