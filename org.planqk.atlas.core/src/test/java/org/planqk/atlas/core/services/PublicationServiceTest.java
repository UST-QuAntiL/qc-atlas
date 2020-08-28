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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class PublicationServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private PublicationService publicationService;
    @Autowired
    private AlgorithmService algorithmService;

    @Test
    void createPublication() {
        Publication publication = getFullPublication("publicationTitle");

        Publication storedPublication = publicationService.create(publication);
        assertPublicationEquality(storedPublication, publication);
    }

    @Test
    void findPublicationById_ElementNotFound() {
        Assertions.assertThrows(NoSuchElementException.class, () ->
                publicationService.findById(UUID.randomUUID()));
    }

    @Test
    void findPublicationById_ElementFound() {
        Publication publication = getFullPublication("publicationTitle");
        Publication storedPublication = publicationService.create(publication);

        storedPublication = publicationService.findById(storedPublication.getId());

        assertPublicationEquality(storedPublication, publication);
    }

    @Test
    void updatePublication_ElementNotFound() {
        Publication publication = getFullPublication("publicationTitle");
        publication.setId(UUID.randomUUID());
        Assertions.assertThrows(NoSuchElementException.class, () ->
                publicationService.update(publication));
    }

    @Test
    void updatePublication_ElementFound() {
        Publication publication = getFullPublication("publicationTitle");
        Publication comparePublication = getFullPublication("publicationTitle");

        Publication storedPublication = publicationService.create(publication);
        comparePublication.setId(storedPublication.getId());
        storedPublication.setTitle("editedPublicationTitle");
        Publication editedProblemType = publicationService.update(storedPublication);

        assertThat(editedProblemType.getId()).isNotNull();
        assertThat(editedProblemType.getId()).isEqualTo(comparePublication.getId());
        assertThat(editedProblemType.getTitle()).isNotEqualTo(comparePublication.getTitle());
        assertThat(storedPublication.getUrl()).isEqualTo(comparePublication.getUrl());
        assertThat(storedPublication.getDoi()).isEqualTo(comparePublication.getDoi());
        assertThat(storedPublication.getAuthors().stream().filter(author ->
                comparePublication.getAuthors().contains(author))
                .count()).isEqualTo(comparePublication.getAuthors().size());
    }

    @Test
    void deletePublication_ElementNotFound() {
        Assertions.assertThrows(NoSuchElementException.class, () ->
                publicationService.delete(UUID.randomUUID()));
    }

    @Test
    void deletePublication_ElementFound() {
        Publication publication = getFullPublication("publicationTitle");
        Publication storedPublication = publicationService.create(publication);

        Assertions.assertDoesNotThrow(() -> publicationService.findById(storedPublication.getId()));

        publicationService.delete(storedPublication.getId());

        Assertions.assertThrows(NoSuchElementException.class, () ->
                publicationService.findById(storedPublication.getId()));
    }

    @Test
    void deletePublications() {
        Publication publication1 = getFullPublication("publicationTitle1");
        Publication storedPublication1 = publicationService.create(publication1);
        Publication publication2 = getFullPublication("publicationTitle2");
        Publication storedPublication2 = publicationService.create(publication2);

        Set<UUID> publicationIds = new HashSet<>();
        publicationIds.add(storedPublication1.getId());
        publicationIds.add(storedPublication2.getId());

        Assertions.assertDoesNotThrow(() -> publicationService.findById(storedPublication1.getId()));
        Assertions.assertDoesNotThrow(() -> publicationService.findById(storedPublication2.getId()));

        publicationService.deletePublications(publicationIds);

        Assertions.assertThrows(NoSuchElementException.class, () ->
                publicationService.findById(storedPublication1.getId()));
        Assertions.assertThrows(NoSuchElementException.class, () ->
                publicationService.findById(storedPublication2.getId()));
    }

    @Test
    void findLinkedAlgorithms_PublicationNotFound() {
        Assertions.assertThrows(NoSuchElementException.class, () ->
                publicationService.findLinkedAlgorithms(UUID.randomUUID(), Pageable.unpaged()));
    }

    @Test
    void findLinkedAlgorithms_PublicationFound() {
        Algorithm algorithm = new ClassicAlgorithm();
        algorithm.setName("testName");
        algorithm.setComputationModel(ComputationModel.CLASSIC);

        Publication publication = getFullPublication("publicationTitle");
        Set<Algorithm> algorithms = new HashSet<>();
        algorithms.add(algorithm);
        publication.setAlgorithms(algorithms);
        Publication storedPublication = publicationService.create(publication);

        algorithm.addPublication(publication);

        Algorithm storedAlgorithm = algorithmService.create(algorithm);

        Set<Algorithm> publicationAlgorithms = storedPublication.getAlgorithms();

        publicationAlgorithms.forEach(algo -> {
            assertThat(algo.getId()).isEqualTo(storedAlgorithm.getId());
        });
    }

    @Test
    void findLinkedImplementations_PublicationNotFound() {
        Assertions.assertThrows(NoSuchElementException.class, () ->
                publicationService.findLinkedImplementations(UUID.randomUUID(), Pageable.unpaged()));
    }

    // @Test
    void findLinkedImplementations_PublicationFound() {
        // TODO
    }

    private void assertPublicationEquality(Publication dbPublication, Publication comparePublication) {
        assertThat(dbPublication.getId()).isNotNull();
        assertThat(dbPublication.getTitle()).isEqualTo(comparePublication.getTitle());
        assertThat(dbPublication.getUrl()).isEqualTo(comparePublication.getUrl());
        assertThat(dbPublication.getDoi()).isEqualTo(comparePublication.getDoi());
        assertThat(dbPublication.getAuthors().stream().filter(author ->
                comparePublication.getAuthors().contains(author))
                .count()).isEqualTo(comparePublication.getAuthors().size());
    }

    static Publication getFullPublication(String title) {
        Publication publication = new Publication();
        publication.setTitle(title);
        publication.setUrl("http://example.com");
        publication.setDoi("doi");
        List<String> publicationAuthors = new ArrayList<>();
        publicationAuthors.add("publicationAuthor1");
        publication.setAuthors(publicationAuthors);
        return publication;
    }
}
