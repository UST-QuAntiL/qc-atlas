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

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.AlgorithmRelation;
import org.planqk.atlas.core.model.ApplicationArea;
import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.model.Publication;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface AlgorithmService {

    @Transactional
    Algorithm create(Algorithm algorithm);

    Page<Algorithm> findAll(Pageable pageable, String search);

    Algorithm findById(UUID algorithmId);

    @Transactional
    Algorithm update(Algorithm algorithm);

    @Transactional
    void delete(UUID algorithmId);

    Page<AlgorithmRelation> findLinkedAlgorithmRelations(UUID algorithmId, Pageable pageable);

    Page<PatternRelation> findLinkedPatternRelations(UUID algorithmId, Pageable pageable);

    Page<Publication> findLinkedPublications(UUID algorithmId, Pageable pageable);

    Page<ProblemType> findLinkedProblemTypes(UUID algorithmId, Pageable pageable);

    Page<ApplicationArea> findLinkedApplicationAreas(UUID algorithmId, Pageable pageable);
}
