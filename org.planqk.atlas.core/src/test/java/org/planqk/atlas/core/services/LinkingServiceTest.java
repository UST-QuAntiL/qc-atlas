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

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ApplicationArea;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.model.ClassicImplementation;
import org.planqk.atlas.core.model.CloudService;
import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.model.QuantumComputationModel;
import org.planqk.atlas.core.model.SoftwarePlatform;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LinkingServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private LinkingService linkingService;

    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private ImplementationService implementationService;

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private ProblemTypeService problemTypeService;

    @Autowired
    private ApplicationAreaService applicationAreaService;

    @Autowired
    private PatternRelationService patternRelationService;

    @Autowired
    private SoftwarePlatformService softwarePlatformService;

    @Autowired
    private CloudServiceService cloudServiceService;

    @Autowired
    private ComputeResourceService computeResourceService;

    @Test
    void linkImplementationAndAlgorithm() {
        Algorithm algorithm = getCreatedAlgorithm();
        Implementation implementation = getCreatedImplementation();

        linkingService.linkImplementationAndAlgorithm(implementation.getId(), algorithm.getId());

        var implementations = implementationService.findByImplementedAlgorithm(algorithm.getId(),Pageable.unpaged()).toSet();

        assertThat(implementations.size()).isEqualTo(1);
    }

    @Test
    void unlinkImplementationAndAlgorithm() {
        Algorithm algorithm = getCreatedAlgorithm();
        Implementation implementation = getCreatedImplementation();

        linkingService.linkImplementationAndAlgorithm(implementation.getId(), algorithm.getId());

        var implementations = implementationService.findByImplementedAlgorithm(algorithm.getId(),Pageable.unpaged()).toSet();

        assertThat(implementations.size()).isEqualTo(1);

        linkingService.unlinkImplementationAndAlgorithm(implementation.getId(), algorithm.getId());

        implementations = implementationService.findByImplementedAlgorithm(algorithm.getId(),Pageable.unpaged()).toSet();

        assertThat(implementations.size()).isEqualTo(0);
    }

    @Test
    void linkAlgorithmAndPublication() {
        Algorithm algorithm = getCreatedAlgorithm();
        Publication publication = getCreatedPublication();

        linkingService.linkAlgorithmAndPublication(algorithm.getId(), publication.getId());

        var publications = algorithmService.findLinkedPublications(
                algorithm.getId(), Pageable.unpaged()).toSet();

        assertThat(publications.size()).isEqualTo(1);
    }

    @Test
    void linkAlgorithmAndPublication_AlreadyLinked() {
        Algorithm algorithm = getCreatedAlgorithm();
        Publication publication = getCreatedPublication();

        assertDoesNotThrow(() -> linkingService.linkAlgorithmAndPublication(algorithm.getId(), publication.getId()));

        assertThrows(EntityReferenceConstraintViolationException.class, () ->
                linkingService.linkAlgorithmAndPublication(algorithm.getId(), publication.getId()));
    }

    @Test
    void unlinkAlgorithmAndPublication() {
        Algorithm algorithm = getCreatedAlgorithm();
        Publication publication = getCreatedPublication();

        linkingService.linkAlgorithmAndPublication(algorithm.getId(), publication.getId());

        var publications = algorithmService.findLinkedPublications(
                algorithm.getId(), Pageable.unpaged()).toSet();
        assertThat(publications.size()).isEqualTo(1);

        linkingService.unlinkAlgorithmAndPublication(algorithm.getId(), publication.getId());

        publications = algorithmService.findLinkedPublications(
                algorithm.getId(), Pageable.unpaged()).toSet();
        assertThat(publications.size()).isEqualTo(0);
    }

    @Test
    void unlinkAlgorithmAndPublication_NotLinked() {
        Algorithm algorithm = getCreatedAlgorithm();
        Publication publication = getCreatedPublication();

        assertThrows(EntityReferenceConstraintViolationException.class, () ->
                linkingService.unlinkAlgorithmAndPublication(algorithm.getId(), publication.getId()));
    }

    @Test
    void linkAlgorithmAndProblemType() {
        Algorithm algorithm = getCreatedAlgorithm();
        ProblemType problemType = getCreatedProblemType();

        linkingService.linkAlgorithmAndProblemType(algorithm.getId(), problemType.getId());

        var problemTypes = algorithmService.findLinkedProblemTypes(
                algorithm.getId(), Pageable.unpaged()).toSet();

        assertThat(problemTypes.size()).isEqualTo(1);
    }

    @Test
    void linkAlgorithmAndProblemType_AlreadyLinked() {
        Algorithm algorithm = getCreatedAlgorithm();
        ProblemType problemType = getCreatedProblemType();

        assertDoesNotThrow(() -> linkingService.linkAlgorithmAndProblemType(algorithm.getId(), problemType.getId()));

        assertThrows(EntityReferenceConstraintViolationException.class, () ->
                linkingService.linkAlgorithmAndProblemType(algorithm.getId(), problemType.getId()));
    }

    @Test
    void unlinkAlgorithmAndProblemType() {
        Algorithm algorithm = getCreatedAlgorithm();
        ProblemType problemType = getCreatedProblemType();

        linkingService.linkAlgorithmAndProblemType(algorithm.getId(), problemType.getId());

        var problemTypes = algorithmService.findLinkedProblemTypes(
                algorithm.getId(), Pageable.unpaged()).toSet();
        assertThat(problemTypes.size()).isEqualTo(1);

        linkingService.unlinkAlgorithmAndProblemType(algorithm.getId(), problemType.getId());

        problemTypes = algorithmService.findLinkedProblemTypes(
                algorithm.getId(), Pageable.unpaged()).toSet();
        assertThat(problemTypes.size()).isEqualTo(0);
    }

    @Test
    void unlinkAlgorithmAndProblemType_NotLinked() {
        Algorithm algorithm = getCreatedAlgorithm();
        ProblemType problemType = getCreatedProblemType();

        assertThrows(EntityReferenceConstraintViolationException.class, () ->
                linkingService.unlinkAlgorithmAndProblemType(algorithm.getId(), problemType.getId()));
    }

    @Test
    void linkAlgorithmAndApplicationArea() {
        Algorithm algorithm = getCreatedAlgorithm();
        ApplicationArea applicationArea = getCreatedApplicationArea();

        linkingService.linkAlgorithmAndApplicationArea(algorithm.getId(), applicationArea.getId());

        var applicationAreas = algorithmService.findLinkedApplicationAreas(
                algorithm.getId(), Pageable.unpaged()).toSet();

        assertThat(applicationAreas.size()).isEqualTo(1);
    }

    @Test
    void linkAlgorithmAndApplicationArea_AlreadyLinked() {
        Algorithm algorithm = getCreatedAlgorithm();
        ApplicationArea applicationArea = getCreatedApplicationArea();

        assertDoesNotThrow(() -> linkingService.linkAlgorithmAndApplicationArea(algorithm.getId(), applicationArea.getId()));

        assertThrows(EntityReferenceConstraintViolationException.class, () ->
                linkingService.linkAlgorithmAndApplicationArea(algorithm.getId(), applicationArea.getId()));
    }

    @Test
    void unlinkAlgorithmAndApplicationArea() {
        Algorithm algorithm = getCreatedAlgorithm();
        ApplicationArea applicationArea = getCreatedApplicationArea();

        linkingService.linkAlgorithmAndApplicationArea(algorithm.getId(), applicationArea.getId());

        var applicationAreas = algorithmService.findLinkedApplicationAreas(
                algorithm.getId(), Pageable.unpaged()).toSet();
        assertThat(applicationAreas.size()).isEqualTo(1);

        linkingService.unlinkAlgorithmAndApplicationArea(algorithm.getId(), applicationArea.getId());

        applicationAreas = algorithmService.findLinkedApplicationAreas(
                algorithm.getId(), Pageable.unpaged()).toSet();
        assertThat(applicationAreas.size()).isEqualTo(0);
    }

    @Test
    void unlinkAlgorithmAndApplicationArea_NotLinked() {
        Algorithm algorithm = getCreatedAlgorithm();
        ApplicationArea applicationArea = getCreatedApplicationArea();

        assertThrows(EntityReferenceConstraintViolationException.class, () ->
                linkingService.unlinkAlgorithmAndApplicationArea(algorithm.getId(), applicationArea.getId()));
    }

    @Test
    void linkImplementationAndPublication() {
        Publication publication = getCreatedPublication();
        Implementation implementation = getCreatedImplementation();

        linkingService.linkImplementationAndPublication(implementation.getId(), publication.getId());

        var publications = implementationService.findLinkedPublications(
                implementation.getId(), Pageable.unpaged()).toSet();

        assertThat(publications.size()).isEqualTo(1);
    }

    @Test
    void linkImplementationAndPublication_AlreadyLinked() {
        Publication publication = getCreatedPublication();
        Implementation implementation = getCreatedImplementation();

        assertDoesNotThrow(() -> linkingService
                .linkImplementationAndPublication(implementation.getId(), publication.getId()));

        assertThrows(EntityReferenceConstraintViolationException.class, () ->
                linkingService.linkImplementationAndPublication(implementation.getId(), publication.getId()));
    }

    @Test
    void unlinkImplementationAndPublication() {
        Publication publication = getCreatedPublication();
        Implementation implementation = getCreatedImplementation();

        linkingService.linkImplementationAndPublication(implementation.getId(), publication.getId());

        var publications = implementationService.findLinkedPublications(
                implementation.getId(), Pageable.unpaged()).toSet();
        assertThat(publications.size()).isEqualTo(1);

        linkingService.unlinkImplementationAndPublication(implementation.getId(), publication.getId());

        publications = implementationService.findLinkedPublications(
                implementation.getId(), Pageable.unpaged()).toSet();
        assertThat(publications.size()).isEqualTo(0);
    }

    @Test
    void unlinkImplementationAndPublication_NotLinked() {
        Publication publication = getCreatedPublication();
        Implementation implementation = getCreatedImplementation();

        assertThrows(EntityReferenceConstraintViolationException.class, () ->
                linkingService.unlinkImplementationAndPublication(
                        implementation.getId(), publication.getId()));
    }

    @Test
    void linkImplementationAndSoftwarePlatform() {
        SoftwarePlatform softwarePlatform = getCreatedSoftwarePlatform();
        Implementation implementation = getCreatedImplementation();

        linkingService.linkImplementationAndSoftwarePlatform(implementation.getId(), softwarePlatform.getId());

        Set<Implementation> implementations = softwarePlatformService.findLinkedImplementations(
                softwarePlatform.getId(), Pageable.unpaged()).toSet();

        assertThat(implementations.size()).isEqualTo(1);
    }

    @Test
    void linkImplementationAndSoftwarePlatform_AlreadyLinked() {
        SoftwarePlatform softwarePlatform = getCreatedSoftwarePlatform();
        Implementation implementation = getCreatedImplementation();

        assertDoesNotThrow(() -> linkingService
                .linkImplementationAndSoftwarePlatform(implementation.getId(), softwarePlatform.getId()));

        assertThrows(EntityReferenceConstraintViolationException.class, () ->
                linkingService.linkImplementationAndSoftwarePlatform(implementation.getId(), softwarePlatform.getId()));
    }

    @Test
    void unlinkImplementationAndSoftwarePlatform() {
        SoftwarePlatform softwarePlatform = getCreatedSoftwarePlatform();
        Implementation implementation = getCreatedImplementation();

        linkingService.linkImplementationAndSoftwarePlatform(implementation.getId(), softwarePlatform.getId());

        Set<Implementation> implementations = softwarePlatformService.findLinkedImplementations(
                softwarePlatform.getId(), Pageable.unpaged()).toSet();
        assertThat(implementations.size()).isEqualTo(1);

        linkingService.unlinkImplementationAndSoftwarePlatform(implementation.getId(), softwarePlatform.getId());

        implementations = softwarePlatformService.findLinkedImplementations(
                softwarePlatform.getId(), Pageable.unpaged()).toSet();
        assertThat(implementations.size()).isEqualTo(0);
    }

    @Test
    void unlinkImplementationAndSoftwarePlatform_NotLinked() {
        SoftwarePlatform softwarePlatform = getCreatedSoftwarePlatform();
        Implementation implementation = getCreatedImplementation();

        assertThrows(EntityReferenceConstraintViolationException.class, () ->
                linkingService.unlinkImplementationAndSoftwarePlatform(
                        implementation.getId(), softwarePlatform.getId()));
    }

    @Test
    void linkSoftwarePlatformAndCloudService() {
        SoftwarePlatform softwarePlatform = getCreatedSoftwarePlatform();
        CloudService cloudService = getCreatedCloudService();

        linkingService.linkSoftwarePlatformAndCloudService(softwarePlatform.getId(), cloudService.getId());

        Set<CloudService> cloudServices = softwarePlatformService.findLinkedCloudServices(
                softwarePlatform.getId(), Pageable.unpaged()).toSet();

        assertThat(cloudServices.size()).isEqualTo(1);
    }

    @Test
    void linkSoftwarePlatformAndCloudService_AlreadyLinked() {
        SoftwarePlatform softwarePlatform = getCreatedSoftwarePlatform();
        CloudService cloudService = getCreatedCloudService();

        assertDoesNotThrow(() -> linkingService
                .linkSoftwarePlatformAndCloudService(softwarePlatform.getId(), cloudService.getId()));

        assertThrows(EntityReferenceConstraintViolationException.class, () ->
                linkingService.linkSoftwarePlatformAndCloudService(softwarePlatform.getId(), cloudService.getId()));
    }

    @Test
    void unlinkSoftwarePlatformAndCloudService() {
        SoftwarePlatform softwarePlatform = getCreatedSoftwarePlatform();
        CloudService cloudService = getCreatedCloudService();

        linkingService.linkSoftwarePlatformAndCloudService(softwarePlatform.getId(), cloudService.getId());

        Set<CloudService> cloudServices = softwarePlatformService.findLinkedCloudServices(
                softwarePlatform.getId(), Pageable.unpaged()).toSet();
        assertThat(cloudServices.size()).isEqualTo(1);

        linkingService.unlinkSoftwarePlatformAndCloudService(softwarePlatform.getId(), cloudService.getId());

        cloudServices = softwarePlatformService.findLinkedCloudServices(
                softwarePlatform.getId(), Pageable.unpaged()).toSet();
        assertThat(cloudServices.size()).isEqualTo(0);
    }

    @Test
    void unlinkSoftwarePlatformAndCloudService_NotLinked() {
        SoftwarePlatform softwarePlatform = getCreatedSoftwarePlatform();
        CloudService cloudService = getCreatedCloudService();

        assertThrows(EntityReferenceConstraintViolationException.class, () ->
                linkingService.unlinkSoftwarePlatformAndCloudService(
                        softwarePlatform.getId(), cloudService.getId()));
    }

    @Test
    void linkSoftwarePlatformAndComputeResource() {
        SoftwarePlatform softwarePlatform = getCreatedSoftwarePlatform();
        ComputeResource computeResource = getCreatedComputeResource();

        linkingService.linkSoftwarePlatformAndComputeResource(
                softwarePlatform.getId(), computeResource.getId());

        Set<ComputeResource> computeResources = softwarePlatformService.findLinkedComputeResources(
                softwarePlatform.getId(), Pageable.unpaged()).toSet();

        assertThat(computeResources.size()).isEqualTo(1);
    }

    @Test
    void linkSoftwarePlatformAndComputeResource_alreadyLinked() {
        SoftwarePlatform softwarePlatform = getCreatedSoftwarePlatform();
        ComputeResource computeResource = getCreatedComputeResource();

        assertDoesNotThrow(() -> linkingService
                .linkSoftwarePlatformAndComputeResource(softwarePlatform.getId(), computeResource.getId()));

        assertThrows(EntityReferenceConstraintViolationException.class, () ->
                linkingService.linkSoftwarePlatformAndComputeResource(
                        softwarePlatform.getId(), computeResource.getId()));
    }

    @Test
    void unlinkSoftwarePlatformAndComputeResource() {
        SoftwarePlatform softwarePlatform = getCreatedSoftwarePlatform();
        ComputeResource computeResource = getCreatedComputeResource();

        linkingService.linkSoftwarePlatformAndComputeResource(
                softwarePlatform.getId(), computeResource.getId());

        Set<ComputeResource> computeResources = softwarePlatformService.findLinkedComputeResources(
                softwarePlatform.getId(), Pageable.unpaged()).toSet();
        assertThat(computeResources.size()).isEqualTo(1);

        linkingService.unlinkSoftwarePlatformAndComputeResource(
                softwarePlatform.getId(), computeResource.getId());

        computeResources = softwarePlatformService.findLinkedComputeResources(
                softwarePlatform.getId(), Pageable.unpaged()).toSet();
        assertThat(computeResources.size()).isEqualTo(0);
    }

    @Test
    void unlinkSoftwarePlatformAndComputeResource_NotLinked() {
        SoftwarePlatform softwarePlatform = getCreatedSoftwarePlatform();
        ComputeResource computeResource = getCreatedComputeResource();

        assertThrows(EntityReferenceConstraintViolationException.class, () ->
                linkingService.unlinkSoftwarePlatformAndComputeResource(
                        softwarePlatform.getId(), computeResource.getId()));
    }

    @Test
    void linkCloudServiceAndComputeResource() {
        CloudService cloudService = getCreatedCloudService();
        ComputeResource computeResource = getCreatedComputeResource();

        linkingService.linkCloudServiceAndComputeResource(
                cloudService.getId(), computeResource.getId());

        Set<ComputeResource> computeResources = cloudServiceService.findLinkedComputeResources(
                cloudService.getId(), Pageable.unpaged()).toSet();

        assertThat(computeResources.size()).isEqualTo(1);
    }

    @Test
    void linkCloudServiceAndComputeResource_alreadyLinked() {
        CloudService cloudService = getCreatedCloudService();
        ComputeResource computeResource = getCreatedComputeResource();

        assertDoesNotThrow(() -> linkingService
                .linkCloudServiceAndComputeResource(cloudService.getId(), computeResource.getId()));

        assertThrows(EntityReferenceConstraintViolationException.class, () ->
                linkingService.linkCloudServiceAndComputeResource(
                        cloudService.getId(), computeResource.getId()));
    }

    @Test
    void unlinkCloudServiceAndComputeResource() {
        CloudService cloudService = getCreatedCloudService();
        ComputeResource computeResource = getCreatedComputeResource();

        linkingService.linkCloudServiceAndComputeResource(
                cloudService.getId(), computeResource.getId());

        Set<ComputeResource> computeResources = cloudServiceService.findLinkedComputeResources(
                cloudService.getId(), Pageable.unpaged()).toSet();
        assertThat(computeResources.size()).isEqualTo(1);

        linkingService.unlinkCloudServiceAndComputeResource(
                cloudService.getId(), computeResource.getId());

        computeResources = cloudServiceService.findLinkedComputeResources(
                cloudService.getId(), Pageable.unpaged()).toSet();
        assertThat(computeResources.size()).isEqualTo(0);
    }

    @Test
    void unlinkCloudServiceAndComputeResource_NotLinked() {
        CloudService cloudService = getCreatedCloudService();
        ComputeResource computeResource = getCreatedComputeResource();

        assertThrows(EntityReferenceConstraintViolationException.class, () ->
                linkingService.unlinkCloudServiceAndComputeResource(
                        cloudService.getId(), computeResource.getId()));
    }

    private Algorithm getCreatedAlgorithm() {
        Algorithm algorithm = new ClassicAlgorithm();
        algorithm.setName("algorithmName");
        return algorithmService.create(algorithm);
    }

    private Implementation getCreatedImplementation() {
        Implementation implementation = new ClassicImplementation();
        Algorithm implementedAlgorithm = getCreatedAlgorithm();
        implementation.setName("implementationName");
        return implementationService.create(implementation, implementedAlgorithm.getId());
    }

    private ProblemType getCreatedProblemType() {
        ProblemType problemType = new ProblemType();
        problemType.setName("problemTypeName");
        return problemTypeService.create(problemType);
    }

    private ApplicationArea getCreatedApplicationArea() {
        ApplicationArea applicationArea = new ApplicationArea();
        applicationArea.setName("applicationAreaName");
        return applicationAreaService.create(applicationArea);
    }

    private Publication getCreatedPublication() {
        Publication publication = new Publication();
        publication.setTitle("publicationTitle");
        return publicationService.create(publication);
    }

    private SoftwarePlatform getCreatedSoftwarePlatform() {
        SoftwarePlatform softwarePlatform = new SoftwarePlatform();
        softwarePlatform.setName("softwarePlatformName");
        return softwarePlatformService.create(softwarePlatform);
    }

    private CloudService getCreatedCloudService() {
        CloudService cloudService = new CloudService();
        cloudService.setName("cloudServiceName");
        return cloudServiceService.create(cloudService);
    }

    private ComputeResource getCreatedComputeResource() {
        ComputeResource computeResource = new ComputeResource();
        computeResource.setName("computeResourceName");
        computeResource.setQuantumComputationModel(QuantumComputationModel.QUANTUM_ANNEALING);
        return computeResourceService.create(computeResource);
    }
}
