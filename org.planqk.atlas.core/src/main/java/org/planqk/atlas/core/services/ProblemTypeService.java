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

import java.util.List;
import java.util.UUID;

import org.planqk.atlas.core.model.ProblemType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for operations related to interacting and modifying {@link ProblemType}s in the database.
 */
public interface ProblemTypeService {

    /**
     * Creates a new database entry for a given {@link ProblemType} and save it to the database.
     * <p>
     * The ID of the {@link ProblemType} parameter should be null, since the ID will be generated by the database
     * when creating the entry.
     * The validation for this is done by the Controller layer, which will reject {@link ProblemType}s with a given ID in
     * its create path.
     *
     * @param problemType The {@link ProblemType} object describing a type of problem that could be solved by an {@link org.planqk.atlas.core.model.Algorithm}
     * @return The {@link ProblemType} object that represents the saved status from the database
     */
    @Transactional
    ProblemType create(ProblemType problemType);

    /**
     * Retrieve multiple {@link ProblemType} entries from the database.
     * <p>
     * The amount of entries is based on the given {@link Pageable} parameter.
     * If the {@link Pageable} is unpaged a {@link Page} with all entries is queried.
     * <p>
     * If no search should be executed the search parameter can be left null or empty.
     *
     * @param pageable The page information, namely page size and page number, of the page we want to retrieve
     * @param search The string based on which a search will be executed
     * @return The page of queried {@link ProblemType} entries
     */
    Page<ProblemType> findAll(Pageable pageable, String search);

    /**
     * Find a database entry of a {@link ProblemType} that is already saved in the database.
     * This search is based on the ID the database has given the {@link ProblemType}
     * object when it was created and first saved to the database.
     * <p>
     * If there is no entry found in the database this method will throw a {@link java.util.NoSuchElementException}.
     *
     * @param problemTypeId The ID of the {@link ProblemType} we want to find
     * @return The {@link ProblemType} with the given ID
     */
    ProblemType findById(UUID problemTypeId);

    /**
     * Update an existing {@link ProblemType} database entry by saving the updated {@link ProblemType} object
     * to the the database.
     * <p>
     * The ID of the {@link ProblemType} parameter has to be set to the ID of the database entry we want to update.
     * The validation for this ID to be set is done by the Controller layer, which will reject {@link ProblemType}s
     * without a given ID in its update path.
     * This ID will be used to query the existing {@link ProblemType} entry we want to update.
     * If no {@link ProblemType} entry with the given ID is found this method will throw a
     * {@link java.util.NoSuchElementException}.
     *
     * @param problemType The {@link ProblemType} we want to update with its updated properties
     * @return the updated {@link ProblemType} object that represents the updated status of the database
     */
    @Transactional
    ProblemType update(ProblemType problemType);

    /**
     * Delete an existing {@link ProblemType} entry from the database.
     * This deletion is based on the ID the database has given the {@link ProblemType}
     * when it was created and first saved to the database.
     * <p>
     * All {@link ProblemType}s where the deleted {@link ProblemType} is a parent, will be have their parent problem type
     * set to null.
     * <p>
     * If no entry with the given ID is found this method will throw a {@link java.util.NoSuchElementException}.
     * <p>
     * If the {@link ProblemType} is still referenced by at least one {@link org.planqk.atlas.core.model.Algorithm} a
     * {@link org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException} will be thrown.
     *
     * @param problemTypeId The ID of the {@link ProblemType} we want to delete
     */
    @Transactional
    void delete(UUID problemTypeId);

    /**
     * Get a list of all recursively findable parent problem types for a given {@link ProblemType}.
     *
     * @param problemTypeId The ID of the {@link ProblemType} we want to create the parent list for
     * @return The list of all recursively findable parents for the given {@link ProblemType}
     */
    List<ProblemType> getParentList(UUID problemTypeId);
}
