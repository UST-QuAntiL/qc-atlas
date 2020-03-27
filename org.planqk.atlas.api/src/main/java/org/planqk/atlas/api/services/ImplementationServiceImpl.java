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

package org.planqk.atlas.api.services;

import java.util.Optional;

import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.repository.ImplementationRepository;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class ImplementationServiceImpl implements ImplementationService {

    @Autowired
    private ImplementationRepository repository;

    @Override
    public Implementation save(Implementation implementation) {
        return repository.save(implementation);
    }

    @Override
    public Page<Implementation> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Optional<Implementation> findById(Long implId) {
        return repository.findById(implId);
    }
}
