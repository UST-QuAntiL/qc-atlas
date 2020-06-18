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

import java.util.NoSuchElementException;
import java.util.UUID;

import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.QuantumAlgorithm;
import org.planqk.atlas.core.model.QuantumResource;
import org.planqk.atlas.core.model.QuantumResourceDataType;
import org.planqk.atlas.core.model.QuantumResourceType;
import org.planqk.atlas.core.model.exceptions.ConsistencyException;
import org.planqk.atlas.core.repository.QuantumResourceRepository;
import org.planqk.atlas.core.repository.QuantumResourceTypeRepository;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class QuantumResourceServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private QuantumResourceService resourceService;
    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private QuantumResourceTypeRepository typeRepository;
    @Autowired
    private QuantumResourceRepository resourceRepository;

    @Test
    void testDeleteType_NotLinked() {
        var resourceType = new QuantumResourceType();
        resourceType.setDescription("Hello World");
        resourceType.setName("Test Name");
        resourceType.setDatatype(QuantumResourceDataType.FLOAT);

        var resource = new QuantumResource();
        resource.setQuantumResourceType(resourceType);

        var storedResource = resourceService.addOrUpdateQuantumResource(resource);

        resourceService.deleteQuantumResource(resource.getId());
        this.resourceService.deleteQuantumResourceType(storedResource.getQuantumResourceType().getId());
        assertThat(this.resourceService.findAllResourceTypes(Pageable.unpaged()).get().count()).isEqualTo(0);
    }

    @Test
    void testDeleteType_StillLinked() {
        var resourceType = new QuantumResourceType();
        resourceType.setDescription("Hello World");
        resourceType.setName("Test Name");
        resourceType.setDatatype(QuantumResourceDataType.FLOAT);

        var resource = new QuantumResource();
        resource.setQuantumResourceType(resourceType);

        var storedResource = resourceService.addOrUpdateQuantumResource(resource);

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            this.resourceService.deleteQuantumResourceType(storedResource.getQuantumResourceType().getId());
        });
        assertThat(this.resourceService.findAllResourceTypes(Pageable.unpaged()).get().count()).isEqualTo(1);
    }

    @Test
    void testDeleteAlgorithm() {
        var resourceType = new QuantumResourceType();
        resourceType.setDescription("Hello World");
        resourceType.setName("Test Name");
        resourceType.setDatatype(QuantumResourceDataType.FLOAT);

        var resource = new QuantumResource();
        resource.setQuantumResourceType(resourceType);

        var algo = new QuantumAlgorithm();
        algo.setNisqReady(true);
        algo.setName("MyFirstQuantumAlgorithm");
        algo.setSpeedUp("123");
        algo.setComputationModel(ComputationModel.QUANTUM);
        var storedAlgo = (QuantumAlgorithm) algorithmService.save(algo);

        var storedResource = resourceService.addQuantumResourceToAlgorithm(storedAlgo, resource);

        algorithmService.delete(storedAlgo.getId());

        var resourceOpt = this.resourceRepository.findById(storedResource.getId());
        assertThat(resourceOpt.isPresent()).isFalse();
    }

    @Test
    void testDeleteResource() {
        var resourceType = new QuantumResourceType();
        resourceType.setDescription("Hello World");
        resourceType.setName("Test Name");
        resourceType.setDatatype(QuantumResourceDataType.FLOAT);

        var resource = new QuantumResource();
        resource.setQuantumResourceType(resourceType);

        var algo = new QuantumAlgorithm();
        algo.setNisqReady(true);
        algo.setName("MyFirstQuantumAlgorithm");
        algo.setSpeedUp("123");
        algo.setComputationModel(ComputationModel.QUANTUM);
        var storedAlgo = (QuantumAlgorithm) algorithmService.save(algo);

        var storedResource = resourceService.addQuantumResourceToAlgorithm(storedAlgo, resource);

        this.resourceService.deleteQuantumResource(storedResource.getId());
        assertEquals(0, this.resourceRepository.findAllByAlgorithm_Id(storedAlgo.getId()).size());
        assertEquals(1, this.typeRepository.findAll().size());
        var dbAlgo = algorithmService.findById(storedAlgo.getId());
        assertEquals(storedAlgo.getName(), dbAlgo.getName());
        assertEquals(0, ((QuantumAlgorithm) dbAlgo).getRequiredQuantumResources().size());
    }

    @Test
    void testCreateQuantumResource_FullByUUID() {
        var resourceType = new QuantumResourceType();
        resourceType.setDescription("Hello World");
        resourceType.setName("Test Name");
        resourceType.setDatatype(QuantumResourceDataType.FLOAT);

        var resource = new QuantumResource();
        resource.setQuantumResourceType(resourceType);

        var algo = new QuantumAlgorithm();
        algo.setNisqReady(true);
        algo.setName("MyFirstQuantumAlgorithm");
        algo.setSpeedUp("123");
        algo.setComputationModel(ComputationModel.QUANTUM);
        var storedAlgo = (QuantumAlgorithm) algorithmService.save(algo);

        var storedResource = resourceService.addOrUpdateQuantumResource(resource);

        resourceService.addQuantumResourceToAlgorithm(storedAlgo.getId(), resource.getId());

        var resultAlgo = ((QuantumAlgorithm) algorithmService.findById(algo.getId()));
        assertEquals(1, this.resourceRepository.findAllByAlgorithm_Id(resultAlgo.getId()).size());
        resultAlgo.getRequiredQuantumResources().forEach(resultResource -> {
            assertEquals(storedResource.getId(), resultResource.getId());
            assertEquals(storedResource.getQuantumResourceType().getId(), resultResource.getQuantumResourceType().getId());
        });
        assertEquals(1, resultAlgo.getRequiredQuantumResources().size());
    }

    @Test
    void testCreateQuantumResource_FullByObject() {
        var resourceType = new QuantumResourceType();
        resourceType.setDescription("Hello World");
        resourceType.setName("Test Name");
        resourceType.setDatatype(QuantumResourceDataType.FLOAT);

        var resource = new QuantumResource();
        resource.setQuantumResourceType(resourceType);

        var algo = new QuantumAlgorithm();
        algo.setNisqReady(true);
        algo.setName("MyFirstQuantumAlgorithm");
        algo.setSpeedUp("123");
        algo.setComputationModel(ComputationModel.QUANTUM);
        var storedAlgo = (QuantumAlgorithm) algorithmService.save(algo);

        var storedResource = resourceService.addQuantumResourceToAlgorithm(storedAlgo, resource);

        var resultAlgo = ((QuantumAlgorithm) algorithmService.findById(algo.getId()));
        assertEquals(1, this.resourceRepository.findAllByAlgorithm_Id(resultAlgo.getId()).size());
        resultAlgo.getRequiredQuantumResources().forEach(resultResource -> {
            assertEquals(storedResource.getId(), resultResource.getId());
            assertEquals(storedResource.getQuantumResourceType().getId(), resultResource.getQuantumResourceType().getId());
        });
        assertEquals(1, resultAlgo.getRequiredQuantumResources().size());
    }

    @Test
    void testCreateQuantumResourceType() {
        var resourceType = new QuantumResourceType();
        resourceType.setDescription("Hello World");
        resourceType.setName("Test Name");
        resourceType.setDatatype(QuantumResourceDataType.FLOAT);

        var insertedElem = this.resourceService.addOrUpdateQuantumResourceType(resourceType);

        var elements = this.typeRepository.findAll();
        assertEquals(1, elements.size());
        var testElem = elements.get(0);
        assertEquals(insertedElem.getId(), testElem.getId());
        assertEquals(resourceType.getDatatype(), testElem.getDatatype());
        assertEquals(resourceType.getName(), testElem.getName());
    }

    @Test
    void testFindTypeById_NotFound() {
        assertThrows(NoSuchElementException.class, () -> resourceService.findResourceTypeById(UUID.randomUUID()));
    }

    @Test
    void testFindById_NotFound() {
        assertThrows(NoSuchElementException.class, () -> resourceService.findResourceById(UUID.randomUUID()));
    }

    @Test
    void testFindTypeById() {
        var resourceType = new QuantumResourceType();
        resourceType.setDescription("Hello World");
        resourceType.setName("Test Name");
        resourceType.setDatatype(QuantumResourceDataType.FLOAT);

        var insertedElem = this.resourceService.addOrUpdateQuantumResourceType(resourceType);

        assertThat(resourceService.findResourceTypeById(insertedElem.getId()).getDatatype()).isEqualTo(resourceType.getDatatype());
    }

    @Test
    void testFindById() {
        var resourceType = new QuantumResourceType();
        resourceType.setDescription("Hello World");
        resourceType.setName("Test Name");
        resourceType.setDatatype(QuantumResourceDataType.FLOAT);

        var resource = new QuantumResource();
        resource.setQuantumResourceType(resourceType);

        var algo = new QuantumAlgorithm();
        algo.setNisqReady(true);
        algo.setName("MyFirstQuantumAlgorithm");
        algo.setSpeedUp("123");
        algo.setComputationModel(ComputationModel.QUANTUM);
        var storedAlgo = (QuantumAlgorithm) algorithmService.save(algo);

        var storedResource = resourceService.addQuantumResourceToAlgorithm(storedAlgo, resource);

        assertThat(resourceService.findResourceById(storedResource.getId()).getAlgorithm().getId()).isEqualTo(storedAlgo.getId());
    }

    @Test
    void testFindAll() {
        var resourceType = new QuantumResourceType();
        resourceType.setDescription("Hello World");
        resourceType.setName("Test Name");
        resourceType.setDatatype(QuantumResourceDataType.FLOAT);

        var resource = new QuantumResource();
        resource.setQuantumResourceType(resourceType);

        var algo = new QuantumAlgorithm();
        algo.setNisqReady(true);
        algo.setName("MyFirstQuantumAlgorithm");
        algo.setSpeedUp("123");
        algo.setComputationModel(ComputationModel.QUANTUM);
        var storedAlgo = (QuantumAlgorithm) algorithmService.save(algo);

        var storedResource = resourceService.addQuantumResourceToAlgorithm(storedAlgo, resource);

        var byAlgo = resourceService.findAllResourcesByAlgorithmId(storedAlgo.getId());
        var byAlgoP = resourceService.findAllResourcesByAlgorithmId(storedAlgo.getId(), Pageable.unpaged());

        assertThat(byAlgo.size()).isEqualTo(1);
        assertThat(byAlgoP.getContent().size()).isEqualTo(1);
    }
}
