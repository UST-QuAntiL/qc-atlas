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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.model.ClassicImplementation;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.model.QuantumAlgorithm;
import org.planqk.atlas.core.model.QuantumComputationModel;
import org.planqk.atlas.core.model.QuantumImplementation;
import org.planqk.atlas.core.model.SoftwarePlatform;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.planqk.atlas.core.util.ServiceTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImplementationServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private ImplementationService implementationService;

    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private SoftwarePlatformService softwarePlatformService;

    @Autowired
    private LinkingService linkingService;

    @Test
    void createImplementation_Classic() {
        Algorithm algorithm = new ClassicAlgorithm();
        algorithm.setName("algorithmName");
        algorithm = algorithmService.create(algorithm);

        var implementation = new ClassicImplementation();
        implementation.setName("implementationName");
        implementation.setImplementedAlgorithm(algorithm);

        var storedImplementation = implementationService.create(implementation, algorithm.getId());

        assertThat(storedImplementation.getId()).isNotNull();
        assertThat(storedImplementation).isInstanceOf(ClassicImplementation.class);
        ServiceTestUtils.assertImplementationEquality(storedImplementation, implementation);
    }

    @Test
    void createImplementation_Quantum() {
        QuantumAlgorithm algorithm = new QuantumAlgorithm();
        algorithm.setName("algorithmName");
        algorithm.setQuantumComputationModel(QuantumComputationModel.MEASUREMENT_BASED);
        algorithm = (QuantumAlgorithm) algorithmService.create(algorithm);

        var implementation = new QuantumImplementation();
        implementation.setName("implementationName");
        implementation.setImplementedAlgorithm(algorithm);

        var storedImplementation = (QuantumImplementation) implementationService.create(implementation, algorithm.getId());

        assertThat(storedImplementation.getId()).isNotNull();
        assertThat(storedImplementation).isInstanceOf(QuantumImplementation.class);
        ServiceTestUtils.assertImplementationEquality(storedImplementation, implementation);
    }

    @Test
    void findAllImplementations() {
        Algorithm algorithm = new Algorithm();
        algorithm.setName("algorithmName");
        algorithm = algorithmService.create(algorithm);

        Implementation implementation1 = new Implementation();
        implementation1.setName("implementationName1");
        implementation1.setImplementedAlgorithm(algorithm);
        implementationService.create(implementation1, algorithm.getId());
        Implementation implementation2 = new Implementation();
        implementation2.setName("implementationName2");
        implementation2.setImplementedAlgorithm(algorithm);
        implementationService.create(implementation2, algorithm.getId());

        List<Implementation> implementations = implementationService.findAll(Pageable.unpaged()).getContent();

        assertThat(implementations.size()).isEqualTo(2);
    }

    @Test
    void findImplementationById_ElementFound() {
        Algorithm algorithm = new ClassicAlgorithm();
        algorithm.setName("algorithmName");
        algorithm = algorithmService.create(algorithm);

        var implementation = new ClassicImplementation();
        implementation.setName("implementationName");
        implementation.setImplementedAlgorithm(algorithm);

        var storedImplementation = implementationService.create(implementation, algorithm.getId());

        var foundImplementation = implementationService.findById(implementation.getId());

        assertThat(storedImplementation.getId()).isEqualTo(foundImplementation.getId());
        ServiceTestUtils.assertImplementationEquality(storedImplementation, foundImplementation);
    }

    @Test
    void findImplementationById_ElementNotFound() {
        assertThrows(NoSuchElementException.class, () -> implementationService.findById(UUID.randomUUID()));
    }

    @Test
    void updateImplementation() {
        Algorithm algorithm = new ClassicAlgorithm();
        algorithm.setName("algorithmName");
        algorithm = algorithmService.create(algorithm);

        var implementation = getFullImplementation("implementationName", algorithm);
        var compareImplementation = getFullImplementation("implementationName", algorithm);

        var storedImplementation = implementationService.create(implementation, algorithm.getId());
        compareImplementation.setId(storedImplementation.getId());

        String editName = "editedAlgorithmName";
        storedImplementation.setName(editName);

        var editedImplementation = implementationService.update(storedImplementation);

        assertThat(editedImplementation.getId()).isEqualTo(compareImplementation.getId());
        assertThat(editedImplementation.getName()).isEqualTo(editName);
        assertThat(editedImplementation.getName()).isNotEqualTo(compareImplementation.getId());

        assertThat(editedImplementation.getDescription()).isEqualTo(compareImplementation.getDescription());
        assertThat(editedImplementation.getContributors()).isEqualTo(compareImplementation.getContributors());
        assertThat(editedImplementation.getAssumptions()).isEqualTo(compareImplementation.getAssumptions());
        assertThat(editedImplementation.getParameter()).isEqualTo(compareImplementation.getParameter());
        assertThat(editedImplementation.getDependencies()).isEqualTo(compareImplementation.getDependencies());
        assertThat(editedImplementation.getImplementedAlgorithm().getId())
                .isEqualTo(compareImplementation.getImplementedAlgorithm().getId());
        ServiceTestUtils.assertAlgorithmEquality(
                editedImplementation.getImplementedAlgorithm(), compareImplementation.getImplementedAlgorithm());
    }

    @Test
    void deleteImplementation_ElementFound() {
        Algorithm algorithm = new ClassicAlgorithm();
        algorithm.setName("algorithmName");
        algorithm = algorithmService.create(algorithm);

        var implementation = new ClassicImplementation();
        implementation.setName("implementationName");
        implementation.setImplementedAlgorithm(algorithm);

        var storedImplementation = implementationService.create(implementation, algorithm.getId());

        assertDoesNotThrow(() -> implementationService.findById(storedImplementation.getId()));

        implementationService.delete(storedImplementation.getId());

        assertThrows(NoSuchElementException.class, () -> implementationService.findById(storedImplementation.getId()));
    }

    @Test
    void deleteImplementation_WithReferences() {
        Algorithm algorithm = new ClassicAlgorithm();
        algorithm.setName("algorithmName");
        algorithm = algorithmService.create(algorithm);

        var implementation = new Implementation();
        implementation.setName("implementationName");
        implementation.setImplementedAlgorithm(algorithm);

        implementation = implementationService.create(implementation, algorithm.getId());

        // add publication
        Publication publication = new Publication();
        publication.setTitle("publicationTitle");
        publication.setUrl("http://example.com");
        publication.setDoi("doi");
        List<String> publicationAuthors = new ArrayList<>();
        publicationAuthors.add("publicationAuthor1");
        publication.setAuthors(publicationAuthors);
        publication = publicationService.create(publication);
        linkingService.linkImplementationAndPublication(implementation.getId(), publication.getId());

        // add software platform
        SoftwarePlatform softwarePlatform = new SoftwarePlatform();
        softwarePlatform.setName("softwarePlatformName");
        softwarePlatform = softwarePlatformService.create(softwarePlatform);
        linkingService.linkImplementationAndSoftwarePlatform(implementation.getId(), softwarePlatform.getId());

        Implementation finalImplementation = implementationService.findById(implementation.getId());

        finalImplementation.getPublications().forEach(pub ->
                assertDoesNotThrow(() -> publicationService.findById(pub.getId())));
        finalImplementation.getSoftwarePlatforms().forEach(sp ->
                assertDoesNotThrow(() -> softwarePlatformService.findById(sp.getId())));

        implementationService.delete(finalImplementation.getId());

        assertThrows(NoSuchElementException.class, () ->
                implementationService.findById(finalImplementation.getId()));

        // check if implementation links are removed
        finalImplementation.getPublications().forEach(pub ->
                assertThat(publicationService.findById(pub.getId()).getImplementations().size()).isEqualTo(0));
        finalImplementation.getSoftwarePlatforms().forEach(sp ->
                assertThat(softwarePlatformService.findById(sp.getId()).getImplementations().size()).isEqualTo(0));
    }

    @Test
    void deleteImplementation_ElementNotFound() {
        Algorithm algorithm = new ClassicAlgorithm();
        algorithm.setName("algorithmName");
        algorithm = algorithmService.create(algorithm);

        var implementation = new ClassicImplementation();
        implementation.setName("implementationName");
        implementation.setImplementedAlgorithm(algorithm);
        implementation.setId(UUID.randomUUID());

        assertThrows(NoSuchElementException.class, () -> implementationService.delete(implementation.getId()));
    }

    @Test
    void checkIfImplementationIsOfAlgorithm_IsOfElement() {
        Algorithm algorithm = new Algorithm();
        algorithm.setName("algorithmName");
        Algorithm persistedAlgorithm = algorithmService.create(algorithm);

        Implementation implementation = new Implementation();
        implementation.setName("implementationName");
        implementation.setImplementedAlgorithm(persistedAlgorithm);
        implementationService.create(implementation, algorithm.getId());

        assertDoesNotThrow(() -> implementationService
                .checkIfImplementationIsOfAlgorithm(implementation.getId(), persistedAlgorithm.getId()));
    }

    @Test
    void checkIfImplementationIsOfAlgorithm_IsNotOfElement() {
        Algorithm algorithm1 = new Algorithm();
        algorithm1.setName("algorithmName1");
        Algorithm persistedAlgorithm1 = algorithmService.create(algorithm1);
        Algorithm algorithm2 = new Algorithm();
        algorithm1.setName("algorithmName2");
        Algorithm persistedAlgorithm2 = algorithmService.create(algorithm2);

        Implementation implementation = new Implementation();
        implementation.setName("implementationName");
        implementation.setImplementedAlgorithm(persistedAlgorithm1);
        implementationService.create(implementation, algorithm1.getId());

        assertThrows(NoSuchElementException.class, () -> implementationService
                .checkIfImplementationIsOfAlgorithm(implementation.getId(), persistedAlgorithm2.getId()));
    }

    @Test
    void findByImplementedAlgorithm() {
        Algorithm algorithm = new Algorithm();
        algorithm.setName("algorithmName");
        algorithm = algorithmService.create(algorithm);

        Implementation implementation1 = new Implementation();
        implementation1.setName("implementationName1");
        implementation1.setImplementedAlgorithm(algorithm);
        implementationService.create(implementation1, algorithm.getId());
        Implementation implementation2 = new Implementation();
        implementation2.setName("implementationName2");
        implementation2.setImplementedAlgorithm(algorithm);
        implementationService.create(implementation2, algorithm.getId());

        List<Implementation> implementations = implementationService
                .findByImplementedAlgorithm(algorithm.getId(), Pageable.unpaged()).getContent();

        assertThat(implementations.size()).isEqualTo(2);
    }

    @Test
    void findLinkedSoftwarePlatforms() {
        Algorithm algorithm = new ClassicAlgorithm();
        algorithm.setName("algorithmName");
        algorithm = algorithmService.create(algorithm);

        var implementation = new ClassicImplementation();
        implementation.setName("implementationName");
        implementation.setImplementedAlgorithm(algorithm);

        var storedImplementation = implementationService.create(implementation, algorithm.getId());

        SoftwarePlatform softwarePlatform1 = new SoftwarePlatform();
        softwarePlatform1.setName("softwarePlatformName1");
        softwarePlatform1 = softwarePlatformService.create(softwarePlatform1);
        linkingService.linkImplementationAndSoftwarePlatform(storedImplementation.getId(), softwarePlatform1.getId());
        SoftwarePlatform softwarePlatform2 = new SoftwarePlatform();
        softwarePlatform2.setName("softwarePlatformName1");
        softwarePlatform2 = softwarePlatformService.create(softwarePlatform2);
        linkingService.linkImplementationAndSoftwarePlatform(storedImplementation.getId(), softwarePlatform2.getId());

        var linkedSoftwarePlatforms = implementationService
                .findLinkedSoftwarePlatforms(storedImplementation.getId(), Pageable.unpaged());

        assertThat(linkedSoftwarePlatforms.getTotalElements()).isEqualTo(2);
    }

    @Test
    void findLinkedPublications() {
        Algorithm algorithm = new ClassicAlgorithm();
        algorithm.setName("algorithmName");
        algorithm = algorithmService.create(algorithm);

        var implementation = new ClassicImplementation();
        implementation.setName("implementationName");
        implementation.setImplementedAlgorithm(algorithm);

        var storedImplementation = implementationService.create(implementation, algorithm.getId());

        Publication publication1 = new Publication();
        publication1.setTitle("publicationTitle1");
        publication1 = publicationService.create(publication1);
        linkingService.linkImplementationAndPublication(storedImplementation.getId(), publication1.getId());
        Publication publication2 = new Publication();
        publication2.setTitle("publicationTitle2");
        publication2 = publicationService.create(publication2);
        linkingService.linkImplementationAndPublication(storedImplementation.getId(), publication2.getId());

        var linkedPublications = implementationService
                .findLinkedPublications(storedImplementation.getId(), Pageable.unpaged());

        assertThat(linkedPublications.getTotalElements()).isEqualTo(2);
    }

    private Implementation getFullImplementation(String name, Algorithm implementedAlgorithm) {
        Implementation implementation = new ClassicImplementation();

        implementation.setName(name);
        implementation.setImplementedAlgorithm(implementedAlgorithm);
        implementation.setDescription("description");
        implementation.setContributors("contributors");
        implementation.setAssumptions("assumptions");
        implementation.setParameter("parameter");
        implementation.setDependencies("dependencies");
        implementation.setVersion("version");
        implementation.setLicense("license");
        implementation.setProblemStatement("problemStatement");
        implementation.setInputFormat("inputFormat");
        implementation.setOutputFormat("outputFormat");

        return implementation;
    }
}
