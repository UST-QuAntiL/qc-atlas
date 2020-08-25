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

import java.util.NoSuchElementException;
import java.util.UUID;

import org.planqk.atlas.core.model.ComputeResourcePropertyType;
import org.planqk.atlas.core.repository.ComputeResourcePropertyTypeRepository;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ComputeResourcePropertyTypeServiceImpl implements ComputeResourcePropertyTypeService {

    private final ComputeResourcePropertyTypeRepository computeResourcePropertyTypeRepository;

    @Override
    @Transactional
    public ComputeResourcePropertyType create(ComputeResourcePropertyType computeResourcePropertyType) {
        return computeResourcePropertyTypeRepository.save(computeResourcePropertyType);
    }

    @Override
    public Page<ComputeResourcePropertyType> findAll(Pageable pageable) {
        return computeResourcePropertyTypeRepository.findAll(pageable);
    }

    @Override
    public ComputeResourcePropertyType findById(UUID computeResourcePropertyTypeId) {
        return this.computeResourcePropertyTypeRepository
                .findById(computeResourcePropertyTypeId).orElseThrow(NoSuchElementException::new);
    }

    @Override
    @Transactional
    public ComputeResourcePropertyType update(ComputeResourcePropertyType computeResourcePropertyType) {
        var persistedComputeResourcePropertyType = findById(computeResourcePropertyType.getId());

        persistedComputeResourcePropertyType.setName(computeResourcePropertyType.getName());
        persistedComputeResourcePropertyType.setDescription(computeResourcePropertyType.getDescription());
        persistedComputeResourcePropertyType.setDatatype(computeResourcePropertyType.getDatatype());

        return computeResourcePropertyTypeRepository.save(persistedComputeResourcePropertyType);
    }

    @Override
    @Transactional
    public void delete(UUID computeResourcePropertyTypeId) {
        // TODO throw consistency exception if object is still linked!
        this.computeResourcePropertyTypeRepository.deleteById(computeResourcePropertyTypeId);
    }
}
