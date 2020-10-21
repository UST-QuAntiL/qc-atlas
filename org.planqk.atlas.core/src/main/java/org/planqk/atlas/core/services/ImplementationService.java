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

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.model.SoftwarePlatform;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for operations related to interacting and modifying {@link Implementation}s in the database.
 */
public interface ImplementationService {

    /**
     * Creates a new database entry for a given {@link Implementation} that implements an existing {@link org.planqk.atlas.core.model.Algorithm}
     * and save it to the database.
     * <p>
     * The ID of the {@link Implementation} parameter should be null, since the ID will be generated by the database
     * when creating the entry.
     * The validation for this is done by the Controller layer, which will reject {@link Implementation}s with a given ID in
     * its create path.
     * <p>
     * The {@link org.planqk.atlas.core.model.Algorithm} that is implemented will be queried from the database based on
     * the {@param implementedAlgorithmId} given to this method.
     * If no {@link org.planqk.atlas.core.model.Algorithm} with the given ID exist a {@link java.util.NoSuchElementException}
     * will be thrown.
     *
     * @param implementation The {@link Implementation} that should be saved to the database
     * @param implementedAlgorithmId The ID of the {@link org.planqk.atlas.core.model.Algorithm} this {@link Implementation}
     *                               implements
     * @return The {@link Implementation} object that represents the saved status from the database
     */
    @Transactional
    Implementation create(Implementation implementation, UUID implementedAlgorithmId);

    /**
     * Retrieve multiple {@link Implementation} entries from the database.
     * <p>
     * The amount of entries is based on the given {@link Pageable} parameter.
     * If the {@link Pageable} is unpaged a {@link Page} with all entries is queried.
     *
     * @param pageable The page information, namely page size and page number, of the page we want to retrieve
     * @return The page of queried {@link Implementation} entries
     */
    Page<Implementation> findAll(Pageable pageable);

    /**
     * Find a database entry of a {@link Implementation} that is already saved in the database.
     * This search is based on the ID the database has given the {@link Implementation}
     * object when it was created and first saved to the database.
     * <p>
     * If there is no entry found in the database this method will throw a {@link java.util.NoSuchElementException}.
     *
     * @param implementationId The ID of the {@link Implementation} we want to find
     * @return The {@link Implementation} with the given ID
     */
    Implementation findById(UUID implementationId);

    /**
     * Update an existing {@link Implementation} database entry by saving the updated {@link Implementation} object
     * to the the database.
     * <p>
     * The ID of the {@link Implementation} parameter has to be set to the ID of the database entry we want to update.
     * The validation for this ID to be set is done by the Controller layer, which will reject {@link Implementation}s
     * without a given ID in its update path.
     * This ID will be used to query the existing {@link Implementation} entry we want to update.
     * If no {@link Implementation} entry with the given ID is found this method will throw a
     * {@link java.util.NoSuchElementException}.
     *
     * @param implementation The {@link Implementation} we want to update with its updated properties
     * @return the updated {@link Implementation} object that represents the updated status of the database
     */
    @Transactional
    Implementation update(Implementation implementation);

    /**
     * Delete an existing {@link Implementation} entry from the database.
     * This deletion is based on the ID the database has given the {@link Implementation}
     * when it was created and first saved to the database.
     * <p>
     * When deleting an {@link Implementation} related {@link org.planqk.atlas.core.model.ComputeResourceProperty}s
     * will be deleted together with it.
     * <p>
     * Objects that can be related to multiple {@link Implementation}s will not be deleted.
     * Only the reference to the deleted {@link Implementation} will be removed from these objects.
     * These include {@link Algorithm}s, {@link Publication}s and  {@link SoftwarePlatform}s.
     * <p>
     * If no entry with the given ID is found this method will throw a {@link java.util.NoSuchElementException}.
     *
     * @param implementationId The ID of the {@link Implementation} we want to delete
     */
    @Transactional
    void delete(UUID implementationId);

    /**
     * Checks if a given {@link Implementation} implements a given {@link Algorithm}.
     * <p>
     * If either the {@link Implementation} or the {@link Algorithm} with given IDs could not be found or
     * if a database entry for both could be found but they are not linked a {@link java.util.NoSuchElementException}
     * is thrown.
     *
     * @param implementationId The ID of the {@link Implementation} we want to check
     * @param algorithmId The ID of the {@link Algorithm} we want to check
     */
    void checkIfImplementationIsOfAlgorithm(UUID implementationId, UUID algorithmId);

    /**
     * Retrieve multiple {@link Implementation}s entries from the database which implement a given {@link Algorithm}.
     * If no entries are found an empty page is returned.
     * <p>
     * The amount of entries is based on the given {@link Pageable} parameter.
     * If the {@link Pageable} is unpaged a {@link Page} with all entries is queried.
     * <p>
     * The given {@link Algorithm} is identified through its ID given as a parameter.
     * If no {@link Algorithm} with the given ID can be found a {@link java.util.NoSuchElementException} is thrown.
     *
     * @param algorithmId The ID of the {@link Algorithm} we want find {@link Implementation}s for
     * @param pageable The page information, namely page size and page number, of the page we want to retrieve
     * @return The page of queried {@link Implementation} entries which implement the given {@link Algorithm}
     */
    Page<Implementation> findByImplementedAlgorithm(UUID algorithmId, Pageable pageable);

    /**
     * Retrieve multiple {@link SoftwarePlatform}s entries from the database of {@link SoftwarePlatform}s that are linked
     * to the given {@link Implementation}
     * If no entries are found an empty page is returned.
     * <p>
     * The amount of entries is based on the given {@link Pageable} parameter.
     * If the {@link Pageable} is unpaged a {@link Page} with all entries is queried.
     * <p>
     * The given {@link SoftwarePlatform} is identified through its ID given as a parameter.
     * If no {@link SoftwarePlatform} with the given ID can be found a {@link java.util.NoSuchElementException} is thrown.
     *
     * @param implementationId The ID of the {@link Implementation} we want find linked {@link SoftwarePlatform}s for
     * @param pageable The page information, namely page size and page number, of the page we want to retrieve
     * @return The page of queried {@link SoftwarePlatform} entries which are linked to the {@link Implementation}
     */
    Page<SoftwarePlatform> findLinkedSoftwarePlatforms(UUID implementationId, Pageable pageable);

    /**
     * Retrieve multiple {@link Publication}s entries from the database of {@link Publication}s that are linked
     * to the given {@link Implementation}
     * If no entries are found an empty page is returned.
     * <p>
     * The amount of entries is based on the given {@link Pageable} parameter.
     * If the {@link Pageable} is unpaged a {@link Page} with all entries is queried.
     * <p>
     * The given {@link Publication} is identified through its ID given as a parameter.
     * If no {@link Publication} with the given ID can be found a {@link java.util.NoSuchElementException} is thrown.
     *
     * @param implementationId The ID of the {@link Implementation} we want find linked {@link Publication}s for
     * @param pageable The page information, namely page size and page number, of the page we want to retrieve
     * @return The page of queried {@link Publication} entries which are linked to the {@link Implementation}
     */
    Page<Publication> findLinkedPublications(UUID implementationId, Pageable pageable);
}
