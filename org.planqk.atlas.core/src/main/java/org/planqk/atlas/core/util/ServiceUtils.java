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

package org.planqk.atlas.core.util;

import java.util.Collection;
import java.util.NoSuchElementException;

import org.planqk.atlas.core.model.HasId;

import org.springframework.data.repository.CrudRepository;

public class ServiceUtils {
    public static <T, ID> void throwIfNotExists(ID id, Class<? extends T> resourceClass, CrudRepository<T, ID> repository) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException(resourceClass.getName() +
                    " with ID \"" + id.toString() + "\" does not exist");
        }
    }

    public static <T, ID> T findById(ID id, Class<? extends T> resourceClass, CrudRepository<T, ID> repository) {
        return repository.findById(id).orElseThrow(() -> new NoSuchElementException(resourceClass.getName() +
                " with ID \"" + id.toString() + "\" does not exist"));
    }

    public static <T extends HasId, ID> boolean containsElementWithId(Collection<T> collection, ID id) {
        return collection.stream().anyMatch(p -> p.getId().equals(id));
    }
}
