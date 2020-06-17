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

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.AlgoRelationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface AlgoRelationTypeService {

    @Transactional
    AlgoRelationType save(AlgoRelationType algoRelationType);

    @Transactional
    AlgoRelationType update(UUID id, AlgoRelationType algoRelationType);

    @Transactional
    void delete(UUID id);

    Optional<AlgoRelationType> findOptionalById(UUID id);

    AlgoRelationType findById(UUID id);

    Set<AlgoRelationType> findByName(String name);

    Page<AlgoRelationType> findAll(Pageable pageable);

}
