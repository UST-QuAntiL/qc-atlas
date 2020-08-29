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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.model.ClassicImplementation;
import org.planqk.atlas.core.model.CloudService;
import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.QuantumComputationModel;
import org.planqk.atlas.core.model.SoftwarePlatform;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    //@Test
    void linkAlgorithmAndPublication() {

    }

    //@Test
    void linkAlgorithmAndPublication_AlreadyLinked() {

    }

    //@Test
    void unlinkAlgorithmAndPublication() {

    }

    //@Test
    void unlinkAlgorithmAndPublication_NotLinked() {

    }

    //@Test
    void linkAlgorithmAndProblemType() {

    }

    //@Test
    void linkAlgorithmAndProblemType_AlreadyLinked() {

    }

    //@Test
    void unlinkAlgorithmAndProblemType() {

    }

    //@Test
    void unlinkAlgorithmAndProblemType_NotLinked() {

    }

    //@Test
    void linkAlgorithmAndApplicationArea() {

    }

    //@Test
    void linkAlgorithmAndApplicationArea_AlreadyLinked() {

    }

    //@Test
    void unlinkAlgorithmAndApplicationArea() {

    }

    //@Test
    void unlinkAlgorithmAndApplicationArea_NotLinked() {

    }

    //@Test
    void linkAlgorithmAndPatternRelation() {

    }

    //@Test
    void linkAlgorithmAndPatternRelation_AlreadyLinked() {

    }

    //@Test
    void unlinkAlgorithmAndPatternRelation() {

    }

    //@Test
    void unlinkAlgorithmAndPatternRelation_NotLinked() {

    }

    //@Test
    void linkImplementationAndPublication() {

    }

    //@Test
    void linkImplementationAndPublication_AlreadyLinked() {

    }

    //@Test
    void unlinkImplementationAndPublication() {

    }

    //@Test
    void unlinkImplementationAndPublication_NotLinked() {

    }

    @Test
    void linkImplementationAndSoftwarePlatform() {
        SoftwarePlatform softwarePlatform = getCreatedSoftwarePlatform();
        Implementation implementation = getCreatedImplementation();

        linkingService.linkImplementationAndSoftwarePlatform(implementation.getId(), softwarePlatform.getId());

        Set<Implementation> implementations = softwarePlatformService.findLinkedImplementations(
                softwarePlatform.getId(), Pageable.unpaged()).toSet();

        assertThat(implementations.size()).isEqualTo(1);
        assertThat(implementations.contains(implementation)).isTrue();
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
        assertThat(cloudServices.contains(cloudService)).isTrue();
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
        assertThat(computeResources.contains(computeResource)).isTrue();
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
        assertThat(computeResources.contains(computeResource)).isTrue();
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
        implementation.setImplementedAlgorithm(implementedAlgorithm);
        return implementationService.create(implementation, implementedAlgorithm.getId());
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
