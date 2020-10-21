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

package org.planqk.atlas.core.util;

import java.util.Collection;
import java.util.NoSuchElementException;

import org.planqk.atlas.core.model.HasId;
import org.springframework.data.repository.CrudRepository;

/**
 * Utility class providing generic functions for the service layer
 */
public class ServiceUtils {
    /**
     * Checks if a given object, based on its ID given by the database, exists.
     * <p>
     * Should a object with given ID not exist a NoSuchElementException is thrown.
     *
     * @param id            of object we want to check
     * @param resourceClass the class of the object
     * @param repository    the repository responsible for the data access of the object.
     */
    public static <T, ID> void throwIfNotExists(ID id, Class<? extends T> resourceClass, CrudRepository<T, ID> repository) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException(resourceClass.getName() +
                " with ID \"" + id.toString() + "\" does not exist");
        }
    }

    /**
     * Finds a object in the database based on its ID.
     * <p>
     * Is not element with the given ID found a NoSuchElementException is thrown.
     *
     * @param id            of object we want to find
     * @param resourceClass the class of the object
     * @param repository    the repository responsible for the data access of the object.
     * @return object from the database with the given ID and its properties
     */
    public static <T, ID> T findById(ID id, Class<? extends T> resourceClass, CrudRepository<T, ID> repository) {
        return repository.findById(id).orElseThrow(() -> new NoSuchElementException(resourceClass.getName() +
            " with ID \"" + id.toString() + "\" does not exist"));
    }

    /**
     * Check if a element with a given ID is contained in a collection.
     *
     * @param collection we want to search for element with ID in
     * @param id         of element we want to check
     * @return boolean based on if the element is in the given collection or not
     */
    public static <T extends HasId, ID> boolean containsElementWithId(Collection<T> collection, ID id) {
        return collection.stream().anyMatch(p -> p.getId().equals(id));
    }
}
