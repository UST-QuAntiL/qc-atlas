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

import javax.transaction.Transactional;

import org.planqk.atlas.core.model.AlgorithmRelation;
import org.planqk.atlas.core.model.PatternRelationType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service class for operations related to interacting and modifying {@link PatternRelationType}s in the database.
 */
public interface PatternRelationTypeService {

    /**
     * Creates a new database entry for a given {@link PatternRelationType} and save it to the database.
     * <p>
     * The ID of the {@link PatternRelationType} parameter should be null, since the ID will be generated by the database
     * when creating the entry.
     * The validation for this is done by the Controller layer, which will reject {@link PatternRelationType}s with a given ID in
     * its create path.
     *
     * @param patternRelationType The {@link PatternRelationType} that should be saved to the database
     * @return The {@link PatternRelationType} object that represents the saved status from the database
     */
    @Transactional
    PatternRelationType create(PatternRelationType patternRelationType);

    /**
     * Find a database entry of a {@link PatternRelationType} that is already saved in the database.
     * This search is based on the ID the database has given the {@link PatternRelationType}
     * object when it was created and first saved to the database.
     * <p>
     * If there is no entry found in the database this method will throw a {@link java.util.NoSuchElementException}.
     *
     * @param patternRelationTypeId The ID of the {@link PatternRelationType} we want to find
     * @return The {@link PatternRelationType} with the given ID
     */
    PatternRelationType findById(UUID patternRelationTypeId);

    /**
     * Retrieve multiple {@link PatternRelationType} entries from the database.
     * <p>
     * The amount of entries is based on the given {@link Pageable} parameter.
     * If the {@link Pageable} is unpaged a {@link Page} with all entries is queried.
     *
     * @param pageable The page information, namely page size and page number, of the page we want to retrieve
     * @return The page of queried {@link PatternRelationType} entries
     */
    Page<PatternRelationType> findAll(Pageable pageable);

    /**
     * Update an existing {@link PatternRelationType} database entry by saving the updated {@link PatternRelationType} object
     * to the the database.
     * <p>
     * The ID of the {@link PatternRelationType} parameter has to be set to the ID of the database entry we want to update.
     * The validation for this ID to be set is done by the Controller layer, which will reject {@link PatternRelationType}s
     * without a given ID in its update path.
     * This ID will be used to query the existing {@link PatternRelationType} entry we want to update.
     * If no {@link PatternRelationType} entry with the given ID is found this method will throw a
     * {@link java.util.NoSuchElementException}.
     *
     * @param patternRelationType The {@link PatternRelationType} we want to update with its updated properties
     * @return the updated {@link PatternRelationType} object that represents the updated status of the database
     */
    @Transactional
    PatternRelationType update(PatternRelationType patternRelationType);

    /**
     * Delete an existing {@link PatternRelationType} entry from the database.
     * This deletion is based on the ID the database has given the {@link PatternRelationType}
     * when it was created and first saved to the database.
     * <p>
     * If no entry with the given ID is found this method will throw a {@link java.util.NoSuchElementException}.
     * <p>
     * If the {@link PatternRelationType} is still referenced by at least one {@link AlgorithmRelation} a
     * {@link org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException} will be thrown.
     *
     * @param patternRelationTypeId The ID of the {@link PatternRelationType} we want to delete
     */
    @Transactional
    void delete(UUID patternRelationTypeId);
}
