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

import org.planqk.atlas.core.model.Tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for operations related to interacting and modifying {@link Tag}s in the database.
 */
public interface TagService {

    /**
     * Creates a new database entry for a given {@link Tag} and save it to the database.
     * <p>
     * The value of the {@link Tag} parameter should be set and will function as the unique ID of the {@link Tag}.
     * The validation for this is done by the Controller layer, which will reject {@link Tag}s without a given value in
     * its create path.
     *
     * @param tag The {@link Tag} that should be saved to the database
     * @return The {@link Tag} object that represents the saved status from the database
     */
    @Transactional
    Tag create(Tag tag);

    /**
     * Retrieve multiple {@link Tag} entries from the database.
     * <p>
     * The amount of entries is based on the given {@link Pageable} parameter.
     * If the {@link Pageable} is unpaged a {@link Page} with all entries is queried.
     *
     * @param pageable The page information, namely page size and page number, of the page we want to retrieve
     * @return The page of queried {@link Tag} entries
     */
    Page<Tag> findAll(Pageable pageable);

    /**
     * Retrieve multiple {@link Tag} entries from the database based on if they match the search string parameter.
     * <p>
     * The amount of entries is based on the given {@link Pageable} parameter.
     * If the {@link Pageable} is unpaged a {@link Page} with all entries is queried.
     *
     * @param pageable The page information, namely page size and page number, of the page we want to retrieve
     * @param search The string based on which a search will be executed
     * @return The page of queried {@link Tag} entries which match the search
     */
    Page<Tag> findAllByContent(String search, Pageable pageable);

    /**
     * Retrieve multiple {@link Tag} entries from the database of a given category.
     * <p>
     * The amount of entries is based on the given {@link Pageable} parameter.
     * If the {@link Pageable} is unpaged a {@link Page} with all entries is queried.
     *
     * @param pageable The page information, namely page size and page number, of the page we want to retrieve
     * @param pageable The category we want to find {@link Tags}s for
     * @return The page of queried {@link Tag} entries which have the given category
     */
    Page<Tag> findAllByCategory(String category, Pageable pageable);

    /**
     * Find a database entry of a {@link Tag} that is already saved in the database.
     * This search is based on the ID which is the value of the {@link Tag} that was set and first saved to the database
     * at its creation.
     * <p>
     * If there is no entry found in the database this method will throw a {@link java.util.NoSuchElementException}.
     *
     * @param value The value of the {@link Tag} we want to find functioning as its ID
     * @return The {@link Tag} with the given value as ID
     */
    Tag findByValue(String value);

    /**
     * Add a {@link Tag} to an existing {@link org.planqk.atlas.core.model.Algorithm}.
     * <p>
     * The Tag we want to add does not have to exist before adding it.
     * If the Tag does not exist it will first be created and a database entry for it will be saved, otherwise the existing
     * tag will be added to the given {@link org.planqk.atlas.core.model.Algorithm}.
     * <p>
     * The given {@link org.planqk.atlas.core.model.Algorithm} is identified through its ID given as a parameter.
     * If no {@link org.planqk.atlas.core.model.Algorithm} with the given ID can be found a {@link java.util.NoSuchElementException}
     * is thrown.
     *
     * @param algorithmId The ID of the {@link org.planqk.atlas.core.model.Algorithm} we want to add a {@link Tag} to
     * @param tag The {@link Tag} object we want to add to the {@link org.planqk.atlas.core.model.Algorithm}
     */
    @Transactional
    void addTagToAlgorithm(UUID algorithmId, Tag tag);

    /**
     * Remove an existing {@link Tag} from an existing {@link org.planqk.atlas.core.model.Algorithm}.
     * <p>
     * If the {@link Tag} that should be removed does not already exist this method will throw a {@link java.util.NoSuchElementException}.
     * <p>
     * The given {@link org.planqk.atlas.core.model.Algorithm} is identified through its ID given as a parameter.
     * If no {@link org.planqk.atlas.core.model.Algorithm} with the given ID can be found a {@link java.util.NoSuchElementException}
     * is thrown.
     *
     * @param algorithmId The ID of the {@link org.planqk.atlas.core.model.Algorithm} we want to remove a {@link Tag} from
     * @param tag The {@link Tag} object we want to remove from the {@link org.planqk.atlas.core.model.Algorithm}
     */
    @Transactional
    void removeTagFromAlgorithm(UUID algorithmId, Tag tag);

    /**
     * Add a {@link Tag} to an existing {@link org.planqk.atlas.core.model.Implementation}.
     * <p>
     * The Tag we want to add does not have to exist before adding it.
     * If the Tag does not exist it will first be created and a database entry for it will be saved, otherwise the existing
     * tag will be added to the given {@link org.planqk.atlas.core.model.Implementation}.
     * <p>
     * The given {@link org.planqk.atlas.core.model.Implementation} is identified through its ID given as a parameter.
     * If no {@link org.planqk.atlas.core.model.Implementation} with the given ID can be found a {@link java.util.NoSuchElementException}
     * is thrown.
     *
     * @param implementationId The ID of the {@link org.planqk.atlas.core.model.Implementation} we want to add a {@link Tag} to
     * @param tag The {@link Tag} object we want to add to the {@link org.planqk.atlas.core.model.Implementation}
     */
    @Transactional
    void addTagToImplementation(UUID implementationId, Tag tag);

    /**
     * Remove an existing {@link Tag} from an existing {@link org.planqk.atlas.core.model.Implementation}.
     * <p>
     * If the {@link Tag} that should be removed does not already exist this method will throw a {@link java.util.NoSuchElementException}.
     * <p>
     * <p>
     * The given {@link org.planqk.atlas.core.model.Implementation} is identified through its ID given as a parameter.
     * If no {@link org.planqk.atlas.core.model.Implementation} with the given ID can be found a {@link java.util.NoSuchElementException}
     * is thrown.
     *
     * @param implementationId The ID of the {@link org.planqk.atlas.core.model.Implementation} we want to remove a {@link Tag} from
     * @param tag The {@link Tag} object we want to remove from the {@link org.planqk.atlas.core.model.Implementation}
     */
    @Transactional
    void removeTagFromImplementation(UUID implementationId, Tag tag);
}
