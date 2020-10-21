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

package org.planqk.atlas.core.repository;

import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.Publication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository to access {@link Publication}s available in the data base with different queries.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface PublicationRepository extends JpaRepository<Publication, UUID> {

    default Page<Publication> findAll(String search, Pageable pageable) {
        return findByTitleContainingIgnoreCaseOrDoiContainingIgnoreCaseOrUrlContainingIgnoreCaseOrAuthorsContainingIgnoreCase(search, search, search,
            search, pageable);
    }

    Page<Publication> findByTitleContainingIgnoreCaseOrDoiContainingIgnoreCaseOrUrlContainingIgnoreCaseOrAuthorsContainingIgnoreCase(String title,
                                                                                                                                     String doi,
                                                                                                                                     String url,
                                                                                                                                     String author,
                                                                                                                                     Pageable pageable);

    boolean existsById(UUID id);

    void deleteByIdIn(Set<UUID> ids);

    @Query("SELECT pub " +
        "FROM Publication pub " +
        "JOIN pub.algorithms algos " +
        "WHERE :algoId = algos.id")
    Page<Publication> findPublicationsByAlgorithmId(@Param("algoId") UUID algorithmId, Pageable pageable);

    @Query("SELECT pub " +
        "FROM Publication pub " +
        "JOIN pub.implementations impls " +
        "WHERE :implId = impls.id")
    Page<Publication> findPublicationsByImplementationId(@Param("implId") UUID implementationId, Pageable pageable);
}
