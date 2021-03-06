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

import org.planqk.atlas.core.model.File;
import org.planqk.atlas.core.model.FileImplementationPackage;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.ImplementationPackage;
import org.planqk.atlas.core.repository.ImplementationPackageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

public interface ImplementationPackageService {

    /**
     * Creates a new database entry for a given {@link ImplementationPackage} and save it to the database.
     * <p>
     * The ID of the {@link ImplementationPackageRepository} parameter should be null, since the ID will be generated by
     * the database when creating the entry. The validation for this is done by the Controller layer, which will reject
     * {@link ImplementationPackageRepository}s with a given ID in its create path.
     *
     * @param implementationPackage The {@link ImplementationPackage} that should be saved to the database
     * @return The {@link ImplementationPackageRepository} object that represents the saved status from the database
     */
    @Transactional
    ImplementationPackage create(ImplementationPackage implementationPackage, UUID implementationId);

    /**
     * Update an existing {@link ImplementationPackage} database entry by saving the updated {@link
     * ImplementationPackage} object to the the database.
     * <p>
     * The ID of the {@link ImplementationPackage} parameter has to be set to the ID of the database entry we want to
     * update. The validation for this ID to be set is done by the Controller layer, which will reject {@link
     * ImplementationPackage}s without a given ID in its update path. This ID will be used to query the existing {@link
     * ImplementationPackage} entry we want to update. If no {@link ImplementationPackage} entry with the given ID is
     * found this method will throw a {@link java.util.NoSuchElementException}.
     *
     * @param implementationPackage The {@link ImplementationPackageRepository} we want to update with its updated
     *                              properties
     * @return the updated {@link ImplementationPackageRepository} object that represents the updated status of the
     * database
     */
    @Transactional
    ImplementationPackage update(ImplementationPackage implementationPackage);

    /**
     * Delete an existing {@link ImplementationPackage} entry from the database. This deletion is based on the ID the
     * database has given the {@link ImplementationPackage} when it was created and first saved to the database.
     * <p>
     * When deleting an {@link ImplementationPackage} related {@link FileImplementationPackage}s will be deleted
     * together with it.
     * <p>
     * Objects that can be related to multiple {@link ImplementationPackage}s will not be deleted. Only the reference to
     * the deleted {@link org.planqk.atlas.core.model.Implementation} will be removed from these objects.
     * <p>
     * If no entry with the given ID is found this method will throw a {@link java.util.NoSuchElementException}.
     * <p>
     * If the {@link ImplementationPackage} is still referenced by at least one {@link
     * org.planqk.atlas.core.model.Implementation} a {@link org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException}
     * will be thrown.
     *
     * @param implementationPackageId The ID of the {@link ImplementationPackage} we want to delete
     */
    @Transactional
    void delete(UUID implementationPackageId);

    /**
     * Find a database entry of a {@link ImplementationPackage} that is already saved in the database.
     * <p>
     * If there is no entry found in the database this method will throw a {@link java.util.NoSuchElementException}.
     *
     * @param packageId The ID of the {@link ImplementationPackage} we want to find
     * @return The {@link ImplementationPackage} with the given ID
     */
    ImplementationPackage findById(UUID packageId);

    Page<ImplementationPackage> findImplementationPackagesByImplementationId(UUID implementationId, Pageable pageable);

    void checkIfImplementationPackageIsLinkedToImplementation(UUID packageId, UUID implementationId);

    /**
     * Retrieve zero or one {@link File} entry from the database of {@link File}s that are linked to the given {@link
     * ImplementationPackage} If no entries are found an empty page is returned.
     *
     * @param implementationPackageId The ID of the {@link ImplementationPackage} we want find the linked {@link File}
     *                                for
     * @return The page of queried {@link File} entries which are linked to the {@link Implementation}
     */
    File findLinkedFile(UUID implementationPackageId);

    /**
     * Creates a {@link File} entry in the database from a multipartfile and links it to a given {@link
     * Implementation}.
     *
     * @param implementationPackageId The ID of the {@link Implementation} we want the {@link File} to be linked.
     * @param multipartFile           The multipart from which we want to create a File entity and link it to the {@link
     *                                Implementation}
     * @return The created and linked {@link File}
     */
    File addFileToImplementationPackage(UUID implementationPackageId, MultipartFile multipartFile);
}
