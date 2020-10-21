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

import org.planqk.atlas.core.model.ComputeResourceProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service class for operations related to interacting and modifying {@link ComputeResourceProperty}s in the database.
 */
public interface ComputeResourcePropertyService {

    /**
     * This method should only be used for testing or in this service itself. Use either of the methods {@link
     * #addComputeResourcePropertyToAlgorithm(UUID, ComputeResourceProperty)}, {@link #addComputeResourcePropertyToImplementation(UUID,
     * ComputeResourceProperty)} or {@link #addComputeResourcePropertyToComputeResource(UUID, ComputeResourceProperty)} when trying to create a {@link
     * ComputeResourceProperty} instead.
     * <p>
     * Creates a new database entry for a given {@link ComputeResourceProperty} and save it to the database.
     * <p>
     * The ID of the {@link ComputeResourceProperty} parameter should be null, since the ID will be generated by the database when creating the entry.
     * The validation for this is done by the Controller layer, which will reject {@link ComputeResourceProperty}s with a given ID in its create
     * path.
     * <p>
     * The {@link org.planqk.atlas.core.model.ComputeResourcePropertyType} has to be set and can not be null. However, only the ID of the type has to
     * be set since the correct type object will be queried from the database. This way we can check if the given type exists in the database without
     * another checking step. If the {@link org.planqk.atlas.core.model.ComputeResourcePropertyType} with given ID doesn't exist a {@link
     * java.util.NoSuchElementException} is thrown.
     *
     * @param computeResourceProperty The {@link ComputeResourceProperty} that should be saved to the database
     * @return The {@link ComputeResourceProperty} object that represents the saved status from the database
     */
    @Transactional
    ComputeResourceProperty create(ComputeResourceProperty computeResourceProperty);

    /**
     * Find a database entry of a {@link ComputeResourceProperty} that is already saved in the database. This search is based on the ID the database
     * has given the {@link ComputeResourceProperty} object when it was created and first saved to the database.
     * <p>
     * If there is no entry found in the database this method will throw a {@link java.util.NoSuchElementException}.
     *
     * @param computeResourcePropertyId The ID of the {@link ComputeResourceProperty} we want to find
     * @return The {@link ComputeResourceProperty} with the given ID
     */
    ComputeResourceProperty findById(UUID computeResourcePropertyId);

    /**
     * Update an existing {@link ComputeResourceProperty} database entry by saving the updated {@link ComputeResourceProperty} object to the the
     * database.
     * <p>
     * The ID of the {@link ComputeResourceProperty} parameter has to be set to the ID of the database entry we want to update. The validation for
     * this ID to be set is done by the Controller layer, which will reject {@link ComputeResourceProperty}s without a given ID in its update path.
     * This ID will be used to query the existing {@link ComputeResourceProperty} entry we want to update. If no {@link ComputeResourceProperty} entry
     * with the given ID is found this method will throw a {@link java.util.NoSuchElementException}.
     * <p>
     * The {@link org.planqk.atlas.core.model.ComputeResourcePropertyType} has to be set and can not be null. However, only the ID of the type has to
     * be set since the correct type object will be queried from the database in order to reduce the error margin for user input. This way we can
     * check if the given type exists in the database without another checking step. If the {@link org.planqk.atlas.core.model.ComputeResourcePropertyType}
     * with given ID doesn't exist a {@link java.util.NoSuchElementException} is thrown. In the update process the type will not be updated itself.
     *
     * @param computeResourceProperty The {@link ComputeResourceProperty} we want to update with its updated properties
     * @return the updated {@link ComputeResourceProperty} object that represents the updated status of the database
     */
    @Transactional
    ComputeResourceProperty update(ComputeResourceProperty computeResourceProperty);

    /**
     * Delete an existing {@link ComputeResourceProperty} entry from the database. This deletion is based on the ID the database has given the {@link
     * ComputeResourceProperty} when it was created and first saved to the database.
     * <p>
     * If no entry with the given ID is found this method will throw a {@link java.util.NoSuchElementException}.
     *
     * @param computeResourcePropertyId The ID of the {@link ComputeResourceProperty} we want to delete
     */
    @Transactional
    void delete(UUID computeResourcePropertyId);

    /**
     * Retrieve multiple {@link ComputeResourceProperty}s entries from the database of {@link ComputeResourceProperty}s that are related to {@link
     * org.planqk.atlas.core.model.Algorithm}s. If no entries are found an empty page is returned.
     * <p>
     * The amount of entries is based on the given {@link Pageable} parameter. If the {@link Pageable} is unpaged a {@link Page} with all entries is
     * queried.
     * <p>
     * The given {@link org.planqk.atlas.core.model.Algorithm} is identified through its ID given as a parameter. If no {@link
     * org.planqk.atlas.core.model.Algorithm} with the given ID can be found a {@link java.util.NoSuchElementException} is thrown.
     *
     * @param algorithmId The ID of the {@link org.planqk.atlas.core.model.Algorithm} we want find {@link ComputeResourceProperty}s for
     * @param pageable    The page information, namely page size and page number, of the page we want to retrieve
     * @return The page of queried {@link ComputeResourceProperty} entries which are linked to the {@link org.planqk.atlas.core.model.Algorithm}
     */
    Page<ComputeResourceProperty> findComputeResourcePropertiesOfAlgorithm(UUID algorithmId, Pageable pageable);

    /**
     * Retrieve multiple {@link ComputeResourceProperty}s entries from the database of {@link ComputeResourceProperty}s that are related to {@link
     * org.planqk.atlas.core.model.Implementation}s. If no entries are found an empty page is returned.
     * <p>
     * The amount of entries is based on the given {@link Pageable} parameter. If the {@link Pageable} is unpaged a {@link Page} with all entries is
     * queried.
     * <p>
     * The given {@link org.planqk.atlas.core.model.Implementation} is identified through its ID given as a parameter. If no {@link
     * org.planqk.atlas.core.model.Implementation} with the given ID can be found a {@link java.util.NoSuchElementException} is thrown.
     *
     * @param implementationId The ID of the {@link org.planqk.atlas.core.model.Implementation} we want find {@link ComputeResourceProperty}s for
     * @param pageable         The page information, namely page size and page number, of the page we want to retrieve
     * @return The page of queried {@link ComputeResourceProperty} entries which are linked to the {@link org.planqk.atlas.core.model.Implementation}
     */
    Page<ComputeResourceProperty> findComputeResourcePropertiesOfImplementation(UUID implementationId, Pageable pageable);

    /**
     * Retrieve multiple {@link ComputeResourceProperty}s entries from the database of {@link ComputeResourceProperty}s that are related to {@link
     * org.planqk.atlas.core.model.ComputeResource}s. If no entries are found an empty page is returned.
     * <p>
     * The amount of entries is based on the given {@link Pageable} parameter. If the {@link Pageable} is unpaged a {@link Page} with all entries is
     * queried.
     * <p>
     * The given {@link org.planqk.atlas.core.model.ComputeResource} is identified through its ID given as a parameter. If no {@link
     * org.planqk.atlas.core.model.ComputeResource} with the given ID can be found a {@link java.util.NoSuchElementException} is thrown.
     *
     * @param computeResourceId The ID of the {@link org.planqk.atlas.core.model.ComputeResource} we want find {@link ComputeResourceProperty}s for
     * @param pageable          The page information, namely page size and page number, of the page we want to retrieve
     * @return The page of queried {@link ComputeResourceProperty} entries which are linked to the {@link org.planqk.atlas.core.model.ComputeResource}
     */
    Page<ComputeResourceProperty> findComputeResourcePropertiesOfComputeResource(UUID computeResourceId, Pageable pageable);

    /**
     * Adds a {@link ComputeResourceProperty} to an existing {@link org.planqk.atlas.core.model.Algorithm}.
     * <p>
     * The given {@link ComputeResourceProperty} does not have to exist before adding it to an {@link org.planqk.atlas.core.model.Algorithm}. If the
     * {@link ComputeResourceProperty} does not have its ID set it will be assumed that the {@link ComputeResourceProperty} does not exist beforehand
     * and this method creates a new database entry for the given {@link ComputeResourceProperty} and saves it to the database. If the {@link
     * ComputeResourceProperty} does have a set ID it will be asumed that it already exists and the {@link ComputeResourceProperty} entry will be
     * queried from the database. If no {@link ComputeResourceProperty} entry with the given ID exists a {@link java.util.NoSuchElementException} will
     * be thrown.
     * <p>
     * The given {@link org.planqk.atlas.core.model.Algorithm} is identified through its ID given as a parameter. If no {@link
     * org.planqk.atlas.core.model.Algorithm} with the given ID can be found a {@link java.util.NoSuchElementException} is thrown.
     * <p>
     * The given {@link ComputeResourceProperty} specifies a {@link org.planqk.atlas.core.model.ComputeResourcePropertyDataType} and its value. If
     * these fields are invalid this method will throw a {@link org.planqk.atlas.core.exceptions.InvalidResourceTypeValueException}.
     *
     * @param algorithmId             The ID of the {@link org.planqk.atlas.core.model.Algorithm} we want to add the {@link ComputeResourceProperty}
     *                                to
     * @param computeResourceProperty The {@link ComputeResourceProperty} we want to add
     * @return The {@link ComputeResourceProperty} that has been added to the given {@link org.planqk.atlas.core.model.Algorithm}
     */
    @Transactional
    ComputeResourceProperty addComputeResourcePropertyToAlgorithm(
        UUID algorithmId, ComputeResourceProperty computeResourceProperty);

    /**
     * Adds a {@link ComputeResourceProperty} to an existing {@link org.planqk.atlas.core.model.Implementation}.
     * <p>
     * The given {@link ComputeResourceProperty} does not have to exist before adding it to an {@link org.planqk.atlas.core.model.Implementation}. If
     * the {@link ComputeResourceProperty} does not have its ID set it will be assumed that the {@link ComputeResourceProperty} does not exist
     * beforehand and this method creates a new database entry for the given {@link ComputeResourceProperty} and saves it to the database. If the
     * {@link ComputeResourceProperty} does have a set ID it will be asumed that it already exists and the {@link ComputeResourceProperty} entry will
     * be queried from the database. If no {@link ComputeResourceProperty} entry with the given ID exists a {@link java.util.NoSuchElementException}
     * will be thrown.
     * <p>
     * The given {@link org.planqk.atlas.core.model.Implementation} is identified through its ID given as a parameter. If no {@link
     * org.planqk.atlas.core.model.Implementation} with the given ID can be found a {@link java.util.NoSuchElementException} is thrown.
     * <p>
     * The given {@link ComputeResourceProperty} specifies a {@link org.planqk.atlas.core.model.ComputeResourcePropertyDataType} and its value. If
     * these fields are invalid this method will throw a {@link org.planqk.atlas.core.exceptions.InvalidResourceTypeValueException}.
     *
     * @param implementationId        The ID of the {@link org.planqk.atlas.core.model.Implementation} we want to add the {@link
     *                                ComputeResourceProperty} to
     * @param computeResourceProperty The {@link ComputeResourceProperty} we want to add
     * @return The {@link ComputeResourceProperty} that has been added to the given {@link org.planqk.atlas.core.model.Implementation}
     */
    @Transactional
    ComputeResourceProperty addComputeResourcePropertyToImplementation(
        UUID implementationId, ComputeResourceProperty computeResourceProperty);

    /**
     * Adds a {@link ComputeResourceProperty} to an existing {@link org.planqk.atlas.core.model.ComputeResource}.
     * <p>
     * The given {@link ComputeResourceProperty} does not have to exist before adding it to an {@link org.planqk.atlas.core.model.ComputeResource}. If
     * the {@link ComputeResourceProperty} does not have its ID set it will be assumed that the {@link ComputeResourceProperty} does not exist
     * beforehand and this method creates a new database entry for the given {@link ComputeResourceProperty} and saves it to the database. If the
     * {@link ComputeResourceProperty} does have a set ID it will be asumed that it already exists and the {@link ComputeResourceProperty} entry will
     * be queried from the database. If no {@link ComputeResourceProperty} entry with the given ID exists a {@link java.util.NoSuchElementException}
     * will be thrown.
     * <p>
     * The given {@link org.planqk.atlas.core.model.ComputeResource} is identified through its ID given as a parameter. If no {@link
     * org.planqk.atlas.core.model.ComputeResource} with the given ID can be found a {@link java.util.NoSuchElementException} is thrown.
     * <p>
     * The given {@link ComputeResourceProperty} specifies a {@link org.planqk.atlas.core.model.ComputeResourcePropertyDataType} and its value. If
     * these fields are invalid this method will throw a {@link org.planqk.atlas.core.exceptions.InvalidResourceTypeValueException}.
     *
     * @param computeResourceId       The ID of the {@link org.planqk.atlas.core.model.ComputeResource} we want to add the {@link
     *                                ComputeResourceProperty} to
     * @param computeResourceProperty The {@link ComputeResourceProperty} we want to add
     * @return The {@link ComputeResourceProperty} that has been added to the given {@link org.planqk.atlas.core.model.ComputeResource}
     */
    @Transactional
    ComputeResourceProperty addComputeResourcePropertyToComputeResource(
        UUID computeResourceId, ComputeResourceProperty computeResourceProperty);

    /**
     * Checks if a given {@link ComputeResourceProperty} is linked to a given {@link org.planqk.atlas.core.model.Algorithm}.
     * <p>
     * If either the {@link ComputeResourceProperty} or the {@link org.planqk.atlas.core.model.Algorithm} with given IDs could not be found or if a
     * database entry for both could be found but they are not linked a {@link java.util.NoSuchElementException} is thrown.
     *
     * @param algorithmId               The ID of the {@link org.planqk.atlas.core.model.Algorithm} we want to check
     * @param computeResourcePropertyId The ID of the {@link ComputeResourceProperty} we want to check
     */
    void checkIfComputeResourcePropertyIsOfAlgorithm(UUID algorithmId, UUID computeResourcePropertyId);

    /**
     * Checks if a given {@link ComputeResourceProperty} is linked to a given {@link org.planqk.atlas.core.model.Implementation}.
     * <p>
     * If either the {@link ComputeResourceProperty} or the {@link org.planqk.atlas.core.model.Implementation} with given IDs could not be found or if
     * a database entry for both could be found but they are not linked a {@link java.util.NoSuchElementException} is thrown.
     *
     * @param implementationId          The ID of the {@link org.planqk.atlas.core.model.Implementation} we want to check
     * @param computeResourcePropertyId The ID of the {@link ComputeResourceProperty} we want to check
     */
    void checkIfComputeResourcePropertyIsOfImplementation(UUID implementationId, UUID computeResourcePropertyId);

    /**
     * Checks if a given {@link ComputeResourceProperty} is linked to a given {@link org.planqk.atlas.core.model.ComputeResource}.
     * <p>
     * If either the {@link ComputeResourceProperty} or the {@link org.planqk.atlas.core.model.ComputeResource} with given IDs could not be found or
     * if a database entry for both could be found but they are not linked a {@link java.util.NoSuchElementException} is thrown.
     *
     * @param computeResourceId         The ID of the {@link org.planqk.atlas.core.model.ComputeResource} we want to check
     * @param computeResourcePropertyId The ID of the {@link ComputeResourceProperty} we want to check
     */
    void checkIfComputeResourcePropertyIsOfComputeResource(UUID computeResourceId, UUID computeResourcePropertyId);
}
