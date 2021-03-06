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

import org.planqk.atlas.core.model.ComputeResourcePropertyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for operations related to interacting and modifying {@link ComputeResourcePropertyType}s in the
 * database.
 */
public interface ComputeResourcePropertyTypeService {

    /**
     * Creates a new database entry for a given {@link ComputeResourcePropertyType} and save it to the database.
     * <p>
     * The ID of the {@link ComputeResourcePropertyType} parameter should be null, since the ID will be generated by the
     * database when creating the entry. The validation for this is done by the Controller layer, which will reject
     * {@link ComputeResourcePropertyType}s with a given ID in its create path.
     *
     * @param computeResourcePropertyType The {@link ComputeResourcePropertyType} that should be saved to the database
     * @return The {@link ComputeResourcePropertyType} object that represents the saved status from the database
     */
    @Transactional
    ComputeResourcePropertyType create(ComputeResourcePropertyType computeResourcePropertyType);

    /**
     * Retrieve multiple {@link ComputeResourcePropertyType} entries from the database.
     * <p>
     * The amount of entries is based on the given {@link Pageable} parameter. If the {@link Pageable} is unpaged a
     * {@link Page} with all entries is queried.
     *
     * @param pageable The page information, namely page size and page number, of the page we want to retrieve
     * @return The page of queried {@link ComputeResourcePropertyType} entries
     */
    Page<ComputeResourcePropertyType> findAll(Pageable pageable);

    /**
     * Find a database entry of a {@link ComputeResourcePropertyType} that is already saved in the database. This search
     * is based on the ID the database has given the {@link ComputeResourcePropertyType} object when it was created and
     * first saved to the database.
     * <p>
     * If there is no entry found in the database this method will throw a {@link java.util.NoSuchElementException}.
     *
     * @param computeResourcePropertyTypeId The ID of the {@link ComputeResourcePropertyType} we want to find
     * @return The {@link ComputeResourcePropertyType} with the given ID
     */
    ComputeResourcePropertyType findById(UUID computeResourcePropertyTypeId);

    /**
     * Update an existing {@link ComputeResourcePropertyType} database entry by saving the updated {@link
     * ComputeResourcePropertyType} object to the the database.
     * <p>
     * The ID of the {@link ComputeResourcePropertyType} parameter has to be set to the ID of the database entry we want
     * to update. The validation for this ID to be set is done by the Controller layer, which will reject {@link
     * ComputeResourcePropertyType}s without a given ID in its update path. This ID will be used to query the existing
     * {@link ComputeResourcePropertyType} entry we want to update. If no {@link ComputeResourcePropertyType} entry with
     * the given ID is found this method will throw a {@link java.util.NoSuchElementException}.
     *
     * @param computeResourcePropertyType The {@link ComputeResourcePropertyType} we want to update with its updated
     *                                    properties
     * @return the updated {@link ComputeResourcePropertyType} object that represents the updated status of the database
     */
    @Transactional
    ComputeResourcePropertyType update(ComputeResourcePropertyType computeResourcePropertyType);

    /**
     * Delete an existing {@link ComputeResourcePropertyType} entry from the database. This deletion is based on the ID
     * the database has given the {@link ComputeResourcePropertyType} when it was created and first saved to the
     * database.
     * <p>
     * If no entry with the given ID is found this method will throw a {@link java.util.NoSuchElementException}.
     * <p>
     * If the {@link ComputeResourcePropertyType} is still referenced by at least one {@link
     * org.planqk.atlas.core.model.ComputeResourceProperty} a {@link org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException}
     * will be thrown.
     *
     * @param computeResourcePropertyTypeId The ID of the {@link ComputeResourcePropertyType} we want to delete
     */
    @Transactional
    void delete(UUID computeResourcePropertyTypeId);
}
