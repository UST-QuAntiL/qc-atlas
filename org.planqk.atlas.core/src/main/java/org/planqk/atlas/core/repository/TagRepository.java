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

package org.planqk.atlas.core.repository;

import org.planqk.atlas.core.model.Tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository to access {@link Tag}s available in the data base with different queries.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface TagRepository extends JpaRepository<Tag, String> {

    Tag findByValue(String value);

    Page<Tag> findByCategory(String category, Pageable pageable);

    Page<Tag> findByValueContainingIgnoreCaseOrCategoryContainingIgnoreCase(String value, String key, Pageable pageable);

    boolean existsTagByValue(String value);
}
