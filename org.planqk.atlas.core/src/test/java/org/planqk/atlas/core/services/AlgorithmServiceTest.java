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
import org.planqk.atlas.core.model.AlgorithmRelation;
import org.planqk.atlas.core.model.AlgorithmRelationType;
import org.planqk.atlas.core.model.ApplicationArea;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.model.QuantumAlgorithm;
import org.planqk.atlas.core.model.QuantumComputationModel;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class AlgorithmServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private AlgorithmService algorithmService;

//    @Autowired
//    private TagService tagService;

    @Autowired
    private ProblemTypeService problemTypeService;

    @Autowired
    private PublicationService publicationService;

    @Test
    void createAlgorithm_NoLinks() {
        Algorithm algorithm = getFullAlgorithm("algorithmName");

        Algorithm storedAlgorithm = algorithmService.create(algorithm);

        assertAlgorithmEquality(storedAlgorithm, algorithm);
    }

//    @Test
//    void testAddAlgorithm_WithTags() {
//        Algorithm algorithm = getGenericAlgorithmWithoutReferences("algorithmName");
//
//        Set<Tag> tags = new HashSet<>();
//        Tag tag = new Tag();
//        tag.setKey("tagKey");
//        tag.setValue("tagValue");
//        tags.add(tag);
//        algorithm.setTags(tags);
//
//        Algorithm storedAlgorithm = algorithmService.save(algorithm);
//
//        assertAlgorithmEquality(storedAlgorithm, algorithm);
//
//        storedAlgorithm.getTags().forEach(t -> {
//            assertThat(t.getId()).isNotNull();
//            assertThat(t.getKey()).isEqualTo(tag.getKey());
//            assertThat(t.getValue()).isEqualTo(tag.getValue());
//            Assertions.assertDoesNotThrow(() -> tagService.getTagById(t.getId()));
//            // assertThat(storedAlgorithm).isIn(t.getAlgorithms());
//        });
//    }

    @Test
    void createAlgorithm_WithProblemTypes() {
        Algorithm algorithm = getFullAlgorithm("algorithmName");

        Set<ProblemType> problemTypes = new HashSet<>();
        ProblemType problemType = new ProblemType();
        problemType.setName("testProblemType");
        problemType.setParentProblemType(UUID.randomUUID());
        problemTypes.add(problemType);
        ProblemType problemType2 = new ProblemType();
        problemType2.setName("testProblemType");
        problemType2.setParentProblemType(UUID.randomUUID());
        problemTypes.add(problemType2);
        algorithm.setProblemTypes(problemTypes);

        Algorithm storedAlgorithm = algorithmService.create(algorithm);

        assertAlgorithmEquality(storedAlgorithm, algorithm);

        storedAlgorithm.getProblemTypes().forEach(pt -> {
            assertThat(pt.getId()).isNotNull();
            Assertions.assertDoesNotThrow(() -> problemTypeService.findById(pt.getId()));
        });
    }

    @Test
    void createAlgorithm_WithPublications() {
        Algorithm algorithm = getFullAlgorithm("algorithmName");

        Set<Publication> publications = new HashSet<>();
        Publication publication = new Publication();
        publication.setTitle("testPublicationTitle");
        publication.setUrl("http://example.com");
        publication.setDoi("testDoi");
        List<String> publicationAuthors = new ArrayList<>();
        publicationAuthors.add("test publication author");
        publication.setAuthors(publicationAuthors);
        Set<Algorithm> publicationAlgorithms = new HashSet<>();
        publicationAlgorithms.add(algorithm);
        publications.add(publication);
        algorithm.setPublications(publications);
        Set<ApplicationArea> applicationAreas = new HashSet<>();
        ApplicationArea applicationArea = new ApplicationArea();
        applicationArea.setName("test");
        applicationAreas.add(applicationArea);
        algorithm.setApplicationAreas(applicationAreas);

        Algorithm storedAlgorithm = algorithmService.create(algorithm);

        assertAlgorithmEquality(storedAlgorithm, algorithm);

        Publication finalPublication = publication;
        storedAlgorithm.getPublications().forEach(pub -> {
            assertThat(pub.getId()).isNotNull();
            assertThat(pub.getTitle()).isEqualTo(finalPublication.getTitle());
            assertThat(pub.getUrl()).isEqualTo(finalPublication.getUrl());
            assertThat(pub.getDoi()).isEqualTo(finalPublication.getDoi());
            assertThat(
                    pub.getAuthors().stream().filter(e -> finalPublication.getAuthors().contains(e)).count()
            ).isEqualTo(finalPublication.getAuthors().size());
            Assertions.assertDoesNotThrow(() -> publicationService.findById(pub.getId()));
        });
    }

    @Test
    void findAllAlgorithms() {
        Algorithm algorithm1 = getFullAlgorithm("algorithmName1");
        algorithmService.create(algorithm1);
        Algorithm algorithm2 = getFullAlgorithm("algorithmName2");
        algorithmService.create(algorithm2);

        List<Algorithm> algorithms = algorithmService.findAll(Pageable.unpaged(), null).getContent();

        assertThat(algorithms.size()).isEqualTo(2);
    }

    @Test
    void findAlgorithmById_ElementNotFound() {
        Assertions.assertThrows(NoSuchElementException.class, () ->
                algorithmService.findById(UUID.randomUUID()));
    }

    @Test
    void findAlgorithmById_ElementFound() {
        Algorithm algorithm = getFullAlgorithm("algorithmName");

        Algorithm storedAlgorithm = algorithmService.create(algorithm);

        storedAlgorithm = algorithmService.findById(storedAlgorithm.getId());

        assertAlgorithmEquality(storedAlgorithm, algorithm);
        assertThat(storedAlgorithm).isInstanceOf(ClassicAlgorithm.class);
    }

    @Test
    void updateAlgorithm_ElementFound() {
        Algorithm algorithm = getFullAlgorithm("algorithmName");
        Algorithm compareAlgorithm = getFullAlgorithm("algorithmName");

        Algorithm storedAlgorithm = algorithmService.create(algorithm);
        compareAlgorithm.setId(storedAlgorithm.getId());
        String editName = "editedAlgorithmName";
        storedAlgorithm.setName(editName);
        Algorithm editedAlgorithm = algorithmService.update(storedAlgorithm);

        assertThat(editedAlgorithm.getId()).isNotNull();
        assertThat(editedAlgorithm.getId()).isEqualTo(compareAlgorithm.getId());
        assertThat(editedAlgorithm.getName()).isNotEqualTo(compareAlgorithm.getName());
        assertThat(editedAlgorithm.getName()).isEqualTo(editName);
        assertThat(editedAlgorithm.getAcronym()).isEqualTo(compareAlgorithm.getAcronym());
        assertThat(editedAlgorithm.getIntent()).isEqualTo(compareAlgorithm.getIntent());
        assertThat(editedAlgorithm.getProblem()).isEqualTo(compareAlgorithm.getProblem());
        assertThat(editedAlgorithm.getInputFormat()).isEqualTo(compareAlgorithm.getInputFormat());
        assertThat(editedAlgorithm.getAlgoParameter()).isEqualTo(compareAlgorithm.getAlgoParameter());
        assertThat(editedAlgorithm.getOutputFormat()).isEqualTo(compareAlgorithm.getOutputFormat());
        editedAlgorithm.getSketches().containsAll(compareAlgorithm.getSketches());
        compareAlgorithm.getSketches().containsAll(editedAlgorithm.getSketches());
        assertThat(editedAlgorithm.getSolution()).isEqualTo(compareAlgorithm.getSolution());
        assertThat(editedAlgorithm.getAssumptions()).isEqualTo(compareAlgorithm.getAssumptions());
        assertThat(editedAlgorithm.getComputationModel()).isEqualTo(compareAlgorithm.getComputationModel());
        // application areas now contains a reference to an algorithm, so check for the name:
        assertThat(editedAlgorithm.getApplicationAreas().iterator().next().getName())
                .isEqualTo(compareAlgorithm.getApplicationAreas().iterator().next().getName());
    }

    @Test
    void updateAlgorithm_ElementNotFound() {
        Algorithm algorithm = getFullAlgorithm("algorithmName");
        algorithm.setId(UUID.randomUUID());
        Assertions.assertThrows(NoSuchElementException.class, () ->
                algorithmService.update(algorithm));
    }

    @Test
    void updateAlgorithm_QuantumAlgorithm() {
        QuantumAlgorithm algorithm = new QuantumAlgorithm();
        algorithm.setName("quantumAlgorithmName");
        algorithm.setComputationModel(ComputationModel.QUANTUM);
        algorithm.setNisqReady(false);
        algorithm.setSpeedUp("2");
        algorithm.setQuantumComputationModel(QuantumComputationModel.QUANTUM_ANNEALING);
        QuantumAlgorithm compareAlgorithm = new QuantumAlgorithm();
        compareAlgorithm.setName("quantumAlgorithmName");
        compareAlgorithm.setComputationModel(ComputationModel.QUANTUM);
        compareAlgorithm.setNisqReady(false);
        compareAlgorithm.setSpeedUp("2");
        compareAlgorithm.setQuantumComputationModel(QuantumComputationModel.QUANTUM_ANNEALING);

        QuantumAlgorithm storedAlgorithm = (QuantumAlgorithm) algorithmService.create(algorithm);
        compareAlgorithm.setId(storedAlgorithm.getId());
        String editName = "editedQuantumAlgorithmName";
        storedAlgorithm.setName(editName);
        QuantumAlgorithm editedAlgorithm = (QuantumAlgorithm) algorithmService.update(storedAlgorithm);

        assertThat(editedAlgorithm.getId()).isNotNull();
        assertThat(editedAlgorithm.getId()).isEqualTo(compareAlgorithm.getId());
        assertThat(editedAlgorithm.getName()).isNotEqualTo(compareAlgorithm.getName());
        assertThat(editedAlgorithm.getName()).isEqualTo(editName);
        assertThat(editedAlgorithm.getComputationModel()).isEqualTo(compareAlgorithm.getComputationModel());
        assertThat(editedAlgorithm.isNisqReady()).isEqualTo(compareAlgorithm.isNisqReady());
        assertThat(editedAlgorithm.getSpeedUp()).isEqualTo(compareAlgorithm.getSpeedUp());
        assertThat(editedAlgorithm.getQuantumComputationModel()).isEqualTo(compareAlgorithm.getQuantumComputationModel());
    }

    @Test
    void deleteAlgorithm_NoLinks() {
        Algorithm algorithm = getFullAlgorithm("algorithmName");

        Algorithm storedAlgorithm = algorithmService.create(algorithm);

        Assertions.assertDoesNotThrow(() -> algorithmService.findById(storedAlgorithm.getId()));

        algorithmService.delete(storedAlgorithm.getId());

        Assertions.assertThrows(NoSuchElementException.class, () ->
                algorithmService.findById(storedAlgorithm.getId()));
    }

    @Test
    void deleteAlgorithm_WithLinks() {
        Algorithm algorithm = getFullAlgorithm("algorithmName");

//        Set<Tag> tags = new HashSet<>();
//        Tag tag = new Tag();
//        tag.setKey("tagKey");
//        tag.setValue("tagValue");
//        tags.add(tag);
//        algorithm.setTags(tags);

        Set<ProblemType> problemTypes = new HashSet<>();
        ProblemType problemType = new ProblemType();
        problemType.setName("testProblemType");
        problemType.setParentProblemType(UUID.randomUUID());
        problemTypes.add(problemType);
        algorithm.setProblemTypes(problemTypes);

        Set<Publication> publications = new HashSet<>();
        Publication publication = new Publication();
        publication.setTitle("testPublicationTitle");
        publication.setUrl("http://example.com");
        publication.setDoi("testDoi");
        List<String> publicationAuthors = new ArrayList<>();
        publicationAuthors.add("test publication author");
        publication.setAuthors(publicationAuthors);
        Set<Algorithm> publicationAlgorithms = new HashSet<>();
        publicationAlgorithms.add(algorithm);
        publication.setAlgorithms(publicationAlgorithms);
        publication = publicationService.create(publication);
        publications.add(publication);
        algorithm.setPublications(publications);

        Algorithm storedAlgorithm = algorithmService.create(algorithm);

        Assertions.assertDoesNotThrow(() -> algorithmService.findById(storedAlgorithm.getId()));
//        storedAlgorithm.getTags().forEach(t ->
//            Assertions.assertDoesNotThrow(() -> tagService.getTagById(t.getId())));
        storedAlgorithm.getProblemTypes().forEach(pt ->
                Assertions.assertDoesNotThrow(() -> problemTypeService.findById(pt.getId())));
        storedAlgorithm.getPublications().forEach(pub ->
                Assertions.assertDoesNotThrow(() -> publicationService.findById(pub.getId())));

        algorithmService.delete(storedAlgorithm.getId());

        Assertions.assertThrows(NoSuchElementException.class, () ->
                algorithmService.findById(storedAlgorithm.getId()));
//        storedAlgorithm.getTags().forEach(t ->
//                Assertions.assertDoesNotThrow(() -> tagService.getTagById(t.getId())));
        storedAlgorithm.getProblemTypes().forEach(pt ->
                Assertions.assertDoesNotThrow(() -> problemTypeService.findById(pt.getId())));
        // TODO maybe test with publication used in 2 algos if not done in publication service test
        storedAlgorithm.getPublications().forEach(pub ->
                Assertions.assertDoesNotThrow(() ->
                        publicationService.findById(pub.getId())));
    }
    
    private void assertAlgorithmEquality(Algorithm dbAlgorithm, Algorithm compareAlgorithm) {
        assertThat(dbAlgorithm.getId()).isNotNull();
        assertThat(dbAlgorithm.getName()).isEqualTo(compareAlgorithm.getName());
        assertThat(dbAlgorithm.getAcronym()).isEqualTo(compareAlgorithm.getAcronym());
        assertThat(dbAlgorithm.getIntent()).isEqualTo(compareAlgorithm.getIntent());
        assertThat(dbAlgorithm.getProblem()).isEqualTo(compareAlgorithm.getProblem());
        assertThat(dbAlgorithm.getInputFormat()).isEqualTo(compareAlgorithm.getInputFormat());
        assertThat(dbAlgorithm.getAlgoParameter()).isEqualTo(compareAlgorithm.getAlgoParameter());
        assertThat(dbAlgorithm.getOutputFormat()).isEqualTo(compareAlgorithm.getOutputFormat());
        dbAlgorithm.getSketches().containsAll(compareAlgorithm.getSketches());
        compareAlgorithm.getSketches().containsAll(dbAlgorithm.getSketches());
        assertThat(dbAlgorithm.getSolution()).isEqualTo(compareAlgorithm.getSolution());
        assertThat(dbAlgorithm.getAssumptions()).isEqualTo(compareAlgorithm.getAssumptions());
        assertThat(dbAlgorithm.getComputationModel()).isEqualTo(compareAlgorithm.getComputationModel());
        assertThat(dbAlgorithm.getApplicationAreas()).isEqualTo(compareAlgorithm.getApplicationAreas());
    }

    private void assertAlgorithmRelationEquality(AlgorithmRelation dbRelation, AlgorithmRelation compareRelation) {
        assertThat(dbRelation.getId()).isNotNull();
        assertThat(dbRelation.getDescription()).isEqualTo(compareRelation.getDescription());
        this.assertAlgorithmEquality(dbRelation.getSourceAlgorithm(), compareRelation.getSourceAlgorithm());
        this.assertAlgorithmEquality(dbRelation.getTargetAlgorithm(), compareRelation.getTargetAlgorithm());
        assertThat(dbRelation.getAlgorithmRelationType().getName()).isEqualTo(compareRelation.getAlgorithmRelationType().getName());
    }

    private AlgorithmRelation getGenericAlgorithmRelation(Algorithm sourceAlgorithm, Algorithm targetAlgorithm) {
        AlgorithmRelation algorithmRelation = new AlgorithmRelation();
        algorithmRelation.setDescription("testRelationDescription");
        algorithmRelation.setSourceAlgorithm(sourceAlgorithm);
        algorithmRelation.setTargetAlgorithm(targetAlgorithm);
        AlgorithmRelationType algorithmRelationType = new AlgorithmRelationType();
        algorithmRelationType.setName("testRelation");
        algorithmRelation.setAlgorithmRelationType(algorithmRelationType);
        return algorithmRelation;
    }

    private Algorithm getFullAlgorithm(String name) {
        Algorithm algorithm = new ClassicAlgorithm();
        algorithm.setName(name);
        algorithm.setAcronym("testAcronym");
        algorithm.setIntent("testIntent");
        algorithm.setProblem("testProblem");
        algorithm.setInputFormat("testInputFormat");
        algorithm.setAlgoParameter("testAlgoParameter");
        algorithm.setOutputFormat("testOutputFormat");
        algorithm.setSketches(new ArrayList<>());
        algorithm.setSolution("testSolution");
        algorithm.setAssumptions("testAssumptions");
        algorithm.setComputationModel(ComputationModel.CLASSIC);
        Set<ApplicationArea> applicationAreas = new HashSet<>();
        ApplicationArea applicationArea = new ApplicationArea();
        applicationArea.setName("test");
        applicationAreas.add(applicationArea);
        algorithm.setApplicationAreas(applicationAreas);
        return algorithm;
    }
}
