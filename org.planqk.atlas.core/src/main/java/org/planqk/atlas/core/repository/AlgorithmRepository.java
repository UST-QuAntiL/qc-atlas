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

import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

/**
 * Repository to access {@link Algorithm}s available in the data base with different queries.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface AlgorithmRepository extends RevisionRepository<Algorithm, UUID, Integer>, JpaRepository<Algorithm, UUID> {

    default Page<Algorithm> findAll(String search, Pageable pageable) {
        return findByNameContainingIgnoreCaseOrAcronymContainingIgnoreCaseOrProblemContainingIgnoreCase(search, search, search, pageable);
    }

    Page<Algorithm> findByNameContainingIgnoreCaseOrAcronymContainingIgnoreCaseOrProblemContainingIgnoreCase(String name, String acronym,
                                                                                                             String problem, Pageable pageable);

    @Query("SELECT algo " +
        "FROM Algorithm algo " +
        "JOIN algo.publications pub " +
        "WHERE  pub.id = :pubId")
    Page<Algorithm> findAlgorithmsByPublicationId(@Param("pubId") UUID publicationId, Pageable pageable);

    @Modifying()
    @Query(value = "DELETE FROM algorithm_revisions WHERE rev = :revId AND id = :algoId", nativeQuery = true)
    void deleteAlgorithmRevision(@Param("revId") Integer revisionId, @Param("algoId") UUID algorithmId);

    @Modifying()
    @Query(value = "DELETE FROM algorithm_revisions WHERE id = :algoId", nativeQuery = true)
    void deleteAllAlgorithmRevisions(@Param("algoId") UUID algorithmId);

    @Modifying()
    @Query(value = "DELETE FROM classic_algorithm_revisions WHERE rev = :revId AND id = :algoId", nativeQuery = true)
    void deleteClassicAlgorithmRevision(@Param("revId") Integer revisionId, @Param("algoId") UUID algorithmId);

    @Modifying()
    @Query(value = "DELETE FROM classic_algorithm_revisions WHERE id = :algoId", nativeQuery = true)
    void deleteAllClassicAlgorithmRevisions(@Param("algoId") UUID algorithmId);

    @Modifying()
    @Query(value = "DELETE FROM quantum_algorithm_revisions WHERE rev = :revId AND id = :algoId", nativeQuery = true)
    void deleteQuantumAlgorithmRevision(@Param("revId") Integer revisionId, @Param("algoId") UUID algorithmId);

    @Modifying()
    @Query(value = "DELETE FROM quantum_algorithm_revisions WHERE id = :algoId", nativeQuery = true)
    void deleteAllQuantumAlgorithmRevisions(@Param("algoId") UUID algorithmId);

    @Modifying()
    @Query(value = "DELETE FROM knowledge_artifact_revisions WHERE rev = :revId AND id = :id", nativeQuery = true)
    void deleteKnowledgeArtifactRevision(@Param("revId") Integer revisionId, @Param("id") UUID id);

    @Modifying()
    @Query(value = "DELETE FROM knowledge_artifact_revisions WHERE id = :id", nativeQuery = true)
    void deleteAllKnowledgeArtifactRevisions(@Param("id") UUID id);

    @Modifying()
    @Query(value = "DELETE FROM revinfo WHERE id = :id", nativeQuery = true)
    void deleteRevisionInfo(@Param("id") Integer id);
}
