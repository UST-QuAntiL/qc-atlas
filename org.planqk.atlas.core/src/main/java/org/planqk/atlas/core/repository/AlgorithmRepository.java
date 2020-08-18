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

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ApplicationArea;
import org.planqk.atlas.core.model.ProblemType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repository to access {@link Algorithm}s available in the data base with different queries.
 */
@RepositoryRestResource(exported = false)
public interface AlgorithmRepository extends JpaRepository<Algorithm, UUID> {

    @Query("SELECT algo FROM Algorithm algo JOIN algo.publications pub WHERE  pub.id = :id")
    Page<Algorithm> findAlgorithmsByPublicationId(@Param("id") UUID id, Pageable p);

    Optional<Algorithm> findByName(String name);

    boolean existsAlgorithmById(UUID id);

    default Page<Algorithm> findAll(String search, Pageable pageable) {
        return findByNameContainingIgnoreCaseOrAcronymContainingIgnoreCaseOrProblemContainingIgnoreCase(search, search, search, pageable);
    }

    Page<Algorithm> findByNameContainingIgnoreCaseOrAcronymContainingIgnoreCaseOrProblemContainingIgnoreCase(String name, String acronym, String problem, Pageable pageable);

    @Query("SELECT alg FROM Algorithm alg JOIN alg.publications publication WHERE publication.id = :publicationId")
    Set<Algorithm> getAlgorithmsWithPublicationId(@Param("publicationId") UUID publicationId);

    //Get all publications that refer only to this single algorithm
    @Query(nativeQuery = true, value = "SELECT CAST(algpub.publication_id AS VARCHAR) FROM algorithm_publication algpub JOIN Publication pub ON algpub.publication_id = pub.id JOIN algorithm_publication algpub2 ON pub.id = algpub2.publication_id WHERE algpub2.algorithm_id = :algorithmId GROUP BY algpub.publication_id HAVING count(algpub.algorithm_id) = 1")
    Set<String> getPublicationIdsOfAlgorithm(@Param("algorithmId") UUID algorithmId);

    //Remove all associations of algorithm
    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM algorithm_publication WHERE algorithm_publication.algorithm_id = :algorithmId")
    void deleteAssociationsOfAlgorithm(@Param("algorithmId") UUID algorithmId);

    Set<Algorithm> findAllByProblemTypes(ProblemType problemType);

    Set<Algorithm> findAllByApplicationAreas(ApplicationArea applicationArea);
}
