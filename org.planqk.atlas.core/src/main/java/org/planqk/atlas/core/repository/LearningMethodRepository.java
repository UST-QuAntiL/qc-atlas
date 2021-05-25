/*******************************************************************************
 * Copyright (c) 2021 the qc-atlas contributors.
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

package org.planqk.atlas.core.repository;

import java.util.UUID;

import org.planqk.atlas.core.model.LearningMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository to access {@link LearningMethod}s available in the data base with different queries.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface LearningMethodRepository extends JpaRepository<LearningMethod, UUID> {

    Page<LearningMethod> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT lm " +
                   "FROM LearningMethod lm " +
                   "JOIN lm.algorithms algos " +
                   "WHERE algos.id = :algoId")
    Page<LearningMethod> findLearningMethodByAlgorithmId(@Param("algoId") UUID algorithmId, Pageable pageable);
}
