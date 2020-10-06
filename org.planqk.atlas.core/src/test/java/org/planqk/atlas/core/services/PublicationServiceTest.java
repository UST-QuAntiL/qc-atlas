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
import org.planqk.atlas.core.model.ClassicImplementation;
import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.planqk.atlas.core.util.ServiceTestUtils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
public class PublicationServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private PublicationService publicationService;
    @Autowired
    private AlgorithmService algorithmService;
    @Autowired
    private ImplementationService implementationService;
    @Autowired
    private LinkingService linkingService;

    @Test
    void createPublication() {
        Publication publication = getFullPublication("publicationTitle");

        Publication storedPublication = publicationService.create(publication);
        ServiceTestUtils.assertPublicationEquality(storedPublication, publication);
    }

    @Test
    void findAllPublications() {
        Publication publication1 = getFullPublication("publicationTitle1");
        publicationService.create(publication1);
        Publication publication2 = getFullPublication("publicationTitle2");
        publicationService.create(publication2);

        var publications = publicationService.findAll(Pageable.unpaged(), "").getContent();

        assertThat(publications.size()).isEqualTo(2);
    }

    @Test
    void findAllPublications_Search() {
        Publication publication1 = getFullPublication("publicationTitle1");
        Publication storedPublication1 = publicationService.create(publication1);
        Publication publication2 = getFullPublication("publicationTitle2");
        Publication storedPublication2 = publicationService.create(publication2);

        var publications = publicationService.findAll(Pageable.unpaged(), "1").getContent();

        assertThat(publications.size()).isEqualTo(1);
        publications.forEach(pub -> {
            assertThat(pub.getId()).isEqualTo(storedPublication1.getId());
            ServiceTestUtils.assertPublicationEquality(pub, storedPublication1);
        });
    }

    @Test
    void findPublicationById_ElementNotFound() {
        assertThrows(NoSuchElementException.class, () ->
                publicationService.findById(UUID.randomUUID()));
    }

    @Test
    void findPublicationById_ElementFound() {
        Publication publication = getFullPublication("publicationTitle");
        Publication storedPublication = publicationService.create(publication);

        storedPublication = publicationService.findById(storedPublication.getId());

        assertThat(storedPublication.getId()).isNotNull();
        ServiceTestUtils.assertPublicationEquality(storedPublication, publication);
    }

    @Test
    void updatePublication_ElementNotFound() {
        Publication publication = getFullPublication("publicationTitle");
        publication.setId(UUID.randomUUID());
        assertThrows(NoSuchElementException.class, () ->
                publicationService.update(publication));
    }

    @Test
    void updatePublication_ElementFound() {
        Publication publication = getFullPublication("publicationTitle");
        Publication comparePublication = getFullPublication("publicationTitle");

        Publication storedPublication = publicationService.create(publication);
        comparePublication.setId(storedPublication.getId());
        String editedTitle = "editedPublicationTitle";
        storedPublication.setTitle(editedTitle);
        Publication editedProblemType = publicationService.update(storedPublication);

        assertThat(editedProblemType.getId()).isNotNull();
        assertThat(editedProblemType.getId()).isEqualTo(comparePublication.getId());
        assertThat(editedProblemType.getTitle()).isNotEqualTo(comparePublication.getTitle());
        assertThat(editedProblemType.getTitle()).isEqualTo(editedTitle);
        assertThat(storedPublication.getUrl()).isEqualTo(comparePublication.getUrl());
        assertThat(storedPublication.getDoi()).isEqualTo(comparePublication.getDoi());

        ServiceTestUtils.assertCollectionEquality(storedPublication.getAuthors(), comparePublication.getAuthors());
    }

    @Test
    void deletePublication_ElementNotFound() {
        assertThrows(NoSuchElementException.class, () ->
                publicationService.delete(UUID.randomUUID()));
    }

    @Test
    void deletePublication_ElementFound() {
        Publication publication = getFullPublication("publicationTitle");
        Publication storedPublication = publicationService.create(publication);

        assertDoesNotThrow(() -> publicationService.findById(storedPublication.getId()));

        publicationService.delete(storedPublication.getId());

        assertThrows(NoSuchElementException.class, () ->
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

        assertDoesNotThrow(() -> publicationService.findById(storedPublication1.getId()));
        assertDoesNotThrow(() -> publicationService.findById(storedPublication2.getId()));

        publicationService.deletePublications(publicationIds);

        assertThrows(NoSuchElementException.class, () ->
                publicationService.findById(storedPublication1.getId()));
        assertThrows(NoSuchElementException.class, () ->
                publicationService.findById(storedPublication2.getId()));
    }

    @Test
    void findLinkedAlgorithms_PublicationNotFound() {
        assertThrows(NoSuchElementException.class, () ->
                publicationService.findLinkedAlgorithms(UUID.randomUUID(), Pageable.unpaged()));
    }

    @Test
    void findLinkedAlgorithms_PublicationFound() {
        Algorithm algorithm = new ClassicAlgorithm();
        algorithm.setName("algorithmName");
        algorithm.setComputationModel(ComputationModel.CLASSIC);
        algorithm = algorithmService.create(algorithm);

        Publication publication = getFullPublication("publicationTitle");
        publication = publicationService.create(publication);

        linkingService.linkAlgorithmAndPublication(algorithm.getId(), publication.getId());

        Set<Algorithm> publicationAlgorithms = publicationService
                .findLinkedAlgorithms(publication.getId(), Pageable.unpaged()).toSet();

        Algorithm finalAlgorithm = algorithm;
        publicationAlgorithms.forEach(algo -> {
            assertThat(algo.getId()).isEqualTo(finalAlgorithm.getId());
        });
    }

    @Test
    void findLinkedImplementations_PublicationNotFound() {
        assertThrows(NoSuchElementException.class, () ->
                publicationService.findLinkedImplementations(UUID.randomUUID(), Pageable.unpaged()));
    }

    @Test
    void findLinkedImplementations_PublicationFound() {
        Algorithm algorithm = new ClassicAlgorithm();
        algorithm.setName("algorithmName");
        algorithm = algorithmService.create(algorithm);

        Implementation implementation = new ClassicImplementation();
        implementation.setName("implementationName");
        implementation = implementationService.create(implementation, algorithm.getId());

        Publication publication = getFullPublication("publicationTitle");
        publication = publicationService.create(publication);

        linkingService.linkImplementationAndPublication(implementation.getId(), publication.getId());

        Set<Implementation> publicationImplementations = publicationService
                .findLinkedImplementations(publication.getId(), Pageable.unpaged()).toSet();

        Implementation finalImplementation = implementation;
        publicationImplementations.forEach(impl -> {
            assertThat(impl.getId()).isEqualTo(finalImplementation.getId());
        });
    }

    @Test
    void checkIfAlgorithmIsLinkedToPublication_IsLinked() {
        Algorithm algorithm = new ClassicAlgorithm();
        algorithm.setName("algorithmName");
        Algorithm persistedAlgorithm = algorithmService.create(algorithm);

        Publication publication = getFullPublication("publicationTitle");
        Publication persistedPublication = publicationService.create(publication);

        linkingService.linkAlgorithmAndPublication(persistedAlgorithm.getId(), persistedPublication.getId());

        assertDoesNotThrow(() -> publicationService
                .checkIfAlgorithmIsLinkedToPublication(persistedPublication.getId(), persistedAlgorithm.getId()));
    }

    @Test
    void checkIfAlgorithmIsLinkedToPublication_IsNotLinked() {
        Algorithm algorithm = new ClassicAlgorithm();
        algorithm.setName("algorithmName");
        Algorithm persistedAlgorithm = algorithmService.create(algorithm);

        Publication publication = getFullPublication("publicationTitle");
        Publication persistedPublication = publicationService.create(publication);

        assertThrows(NoSuchElementException.class, () -> publicationService
                .checkIfAlgorithmIsLinkedToPublication(persistedPublication.getId(), persistedAlgorithm.getId()));
    }

    @Test
    void checkIfImplementationIsLinkedToPublication_IsLinked() {
        Algorithm algorithm = new ClassicAlgorithm();
        algorithm.setName("algorithmName");
        algorithm = algorithmService.create(algorithm);

        Implementation implementation = new ClassicImplementation();
        implementation.setName("implementationName");
        Implementation persistedImplementation = implementationService.create(implementation, algorithm.getId());

        Publication publication = getFullPublication("publicationTitle");
        Publication persistedPublication = publicationService.create(publication);

        linkingService.linkImplementationAndPublication(persistedImplementation.getId(), persistedPublication.getId());

        assertDoesNotThrow(() -> publicationService
                .checkIfImplementationIsLinkedToPublication(persistedPublication.getId(), persistedImplementation.getId()));
    }

    @Test
    void checkIfImplementationIsLinkedToPublication_IsNotLinked() {
        Algorithm algorithm = new ClassicAlgorithm();
        algorithm.setName("algorithmName");
        algorithm = algorithmService.create(algorithm);

        Implementation implementation = new ClassicImplementation();
        implementation.setName("implementationName");
        Implementation persistedImplementation = implementationService.create(implementation, algorithm.getId());

        Publication publication = getFullPublication("publicationTitle");
        Publication persistedPublication = publicationService.create(publication);

        assertThrows(NoSuchElementException.class, () -> publicationService
                .checkIfImplementationIsLinkedToPublication(persistedPublication.getId(), persistedImplementation.getId()));
    }

    private Publication getFullPublication(String title) {
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
