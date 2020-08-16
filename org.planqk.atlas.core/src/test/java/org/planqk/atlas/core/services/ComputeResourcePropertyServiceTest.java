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
import org.planqk.atlas.core.model.ComputeResourceProperty;
import org.planqk.atlas.core.model.ComputeResourcePropertyDataType;
import org.planqk.atlas.core.model.ComputeResourcePropertyType;
import org.planqk.atlas.core.model.QuantumAlgorithm;
import org.planqk.atlas.core.repository.ComputeResourcePropertyRepository;
import org.planqk.atlas.core.repository.ComputeResourcePropertyTypeRepository;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ComputeResourcePropertyServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private ComputeResourcePropertyService resourceService;
    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private ComputeResourcePropertyTypeRepository typeRepository;
    @Autowired
    private ComputeResourcePropertyRepository resourceRepository;

    @Test
    void testDeleteType_NotLinked() {
        var resourceType = new ComputeResourcePropertyType();
        resourceType.setDescription("Hello World");
        resourceType.setName("Test Name");
        resourceType.setDatatype(ComputeResourcePropertyDataType.FLOAT);

        var resource = new ComputeResourceProperty();
        resource.setComputeResourcePropertyType(resourceType);

        var storedResource = resourceService.saveComputeResourceProperty(resource);

        resourceService.deleteComputeResourceProperty(resource.getId());
        this.resourceService.deleteComputeResourcePropertyType(storedResource.getComputeResourcePropertyType().getId());
        assertThat(this.resourceService.findAllComputeResourcePropertyTypes(Pageable.unpaged()).get().count()).isEqualTo(0);
    }

    @Test
    void testDeleteType_StillLinked() {
        var resourceType = new ComputeResourcePropertyType();
        resourceType.setDescription("Hello World");
        resourceType.setName("Test Name");
        resourceType.setDatatype(ComputeResourcePropertyDataType.FLOAT);

        var resource = new ComputeResourceProperty();
        resource.setComputeResourcePropertyType(resourceType);

        var storedResource = resourceService.saveComputeResourceProperty(resource);

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            this.resourceService.deleteComputeResourcePropertyType(storedResource.getComputeResourcePropertyType().getId());
        });
        assertThat(this.resourceService.findAllComputeResourcePropertyTypes(Pageable.unpaged()).get().count()).isEqualTo(1);
    }

    @Test
    void testDeleteAlgorithm() {
        var resourceType = new ComputeResourcePropertyType();
        resourceType.setDescription("Hello World");
        resourceType.setName("Test Name");
        resourceType.setDatatype(ComputeResourcePropertyDataType.FLOAT);

        var resource = new ComputeResourceProperty();
        resource.setComputeResourcePropertyType(resourceType);

        var algo = new QuantumAlgorithm();
        algo.setNisqReady(true);
        algo.setName("MyFirstQuantumAlgorithm");
        algo.setSpeedUp("123");
        algo.setComputationModel(ComputationModel.QUANTUM);
        var storedAlgo = (QuantumAlgorithm) algorithmService.save(algo);

        var storedResource = resourceService.addComputeResourcePropertyToAlgorithm(storedAlgo, resource);

        algorithmService.delete(storedAlgo.getId());

        var resourceOpt = this.resourceRepository.findById(storedResource.getId());
        assertThat(resourceOpt.isPresent()).isFalse();
    }

    @Test
    void testDeleteResource() {
        var resourceType = new ComputeResourcePropertyType();
        resourceType.setDescription("Hello World");
        resourceType.setName("Test Name");
        resourceType.setDatatype(ComputeResourcePropertyDataType.FLOAT);

        var resource = new ComputeResourceProperty();
        resource.setComputeResourcePropertyType(resourceType);

        var algo = new QuantumAlgorithm();
        algo.setNisqReady(true);
        algo.setName("MyFirstQuantumAlgorithm");
        algo.setSpeedUp("123");
        algo.setComputationModel(ComputationModel.QUANTUM);
        var storedAlgo = (QuantumAlgorithm) algorithmService.save(algo);

        var storedResource = resourceService.addComputeResourcePropertyToAlgorithm(storedAlgo, resource);

        this.resourceService.deleteComputeResourceProperty(storedResource.getId());
        assertEquals(0, this.resourceRepository.findAllByAlgorithm_Id(storedAlgo.getId()).size());
        assertEquals(1, this.typeRepository.findAll().size());
        var dbAlgo = algorithmService.findById(storedAlgo.getId());
        assertEquals(storedAlgo.getName(), dbAlgo.getName());
        assertEquals(0, ((QuantumAlgorithm) dbAlgo).getRequiredComputeResourceProperties().size());
    }

    @Test
    void testCreateQuantumResource_FullByUUID() {
        var resourceType = new ComputeResourcePropertyType();
        resourceType.setDescription("Hello World");
        resourceType.setName("Test Name");
        resourceType.setDatatype(ComputeResourcePropertyDataType.FLOAT);

        var resource = new ComputeResourceProperty();
        resource.setComputeResourcePropertyType(resourceType);

        var algo = new QuantumAlgorithm();
        algo.setNisqReady(true);
        algo.setName("MyFirstQuantumAlgorithm");
        algo.setSpeedUp("123");
        algo.setComputationModel(ComputationModel.QUANTUM);
        var storedAlgo = (QuantumAlgorithm) algorithmService.save(algo);

        var storedResource = resourceService.saveComputeResourceProperty(resource);

        resourceService.addComputeResourcePropertyToAlgorithm(storedAlgo.getId(), resource.getId());

        var resultAlgo = ((QuantumAlgorithm) algorithmService.findById(algo.getId()));
        assertEquals(1, this.resourceRepository.findAllByAlgorithm_Id(resultAlgo.getId()).size());
        resultAlgo.getRequiredComputeResourceProperties().forEach(resultResource -> {
            assertEquals(storedResource.getId(), resultResource.getId());
            assertEquals(storedResource.getComputeResourcePropertyType().getId(), resultResource.getComputeResourcePropertyType().getId());
        });
        assertEquals(1, resultAlgo.getRequiredComputeResourceProperties().size());
    }

    @Test
    void testCreateComputingResourceProperty_FullByObject() {
        var resourceType = new ComputeResourcePropertyType();
        resourceType.setDescription("Hello World");
        resourceType.setName("Test Name");
        resourceType.setDatatype(ComputeResourcePropertyDataType.FLOAT);

        var resource = new ComputeResourceProperty();
        resource.setComputeResourcePropertyType(resourceType);

        var algo = new QuantumAlgorithm();
        algo.setNisqReady(true);
        algo.setName("MyFirstQuantumAlgorithm");
        algo.setSpeedUp("123");
        algo.setComputationModel(ComputationModel.QUANTUM);
        var storedAlgo = (QuantumAlgorithm) algorithmService.save(algo);

        var storedResource = resourceService.addComputeResourcePropertyToAlgorithm(storedAlgo, resource);

        var resultAlgo = ((QuantumAlgorithm) algorithmService.findById(algo.getId()));
        assertEquals(1, this.resourceRepository.findAllByAlgorithm_Id(resultAlgo.getId()).size());
        resultAlgo.getRequiredComputeResourceProperties().forEach(resultResource -> {
            assertEquals(storedResource.getId(), resultResource.getId());
            assertEquals(storedResource.getComputeResourcePropertyType().getId(), resultResource.getComputeResourcePropertyType().getId());
        });
        assertEquals(1, resultAlgo.getRequiredComputeResourceProperties().size());
    }

    @Test
    void testCreateComputingResourcePropertyType() {
        var resourceType = new ComputeResourcePropertyType();
        resourceType.setDescription("Hello World");
        resourceType.setName("Test Name");
        resourceType.setDatatype(ComputeResourcePropertyDataType.FLOAT);

        var insertedElem = this.resourceService.saveComputeResourcePropertyType(resourceType);

        var elements = this.typeRepository.findAll();
        assertEquals(1, elements.size());
        var testElem = elements.get(0);
        assertEquals(insertedElem.getId(), testElem.getId());
        assertEquals(resourceType.getDatatype(), testElem.getDatatype());
        assertEquals(resourceType.getName(), testElem.getName());
    }

    @Test
    void testFindTypeById_NotFound() {
        assertThrows(NoSuchElementException.class, () -> resourceService.findComputeResourcePropertyTypeById(UUID.randomUUID()));
    }

    @Test
    void testFindById_NotFound() {
        assertThrows(NoSuchElementException.class, () -> resourceService.findComputeResourcePropertyById(UUID.randomUUID()));
    }

    @Test
    void testFindTypeById() {
        var resourceType = new ComputeResourcePropertyType();
        resourceType.setDescription("Hello World");
        resourceType.setName("Test Name");
        resourceType.setDatatype(ComputeResourcePropertyDataType.FLOAT);

        var insertedElem = this.resourceService.saveComputeResourcePropertyType(resourceType);

        assertThat(resourceService.findComputeResourcePropertyTypeById(insertedElem.getId()).getDatatype()).isEqualTo(resourceType.getDatatype());
    }

    @Test
    void testFindById() {
        var resourceType = new ComputeResourcePropertyType();
        resourceType.setDescription("Hello World");
        resourceType.setName("Test Name");
        resourceType.setDatatype(ComputeResourcePropertyDataType.FLOAT);

        var resource = new ComputeResourceProperty();
        resource.setComputeResourcePropertyType(resourceType);

        var algo = new QuantumAlgorithm();
        algo.setNisqReady(true);
        algo.setName("MyFirstQuantumAlgorithm");
        algo.setSpeedUp("123");
        algo.setComputationModel(ComputationModel.QUANTUM);
        var storedAlgo = (QuantumAlgorithm) algorithmService.save(algo);

        var storedResource = resourceService.addComputeResourcePropertyToAlgorithm(storedAlgo, resource);

        assertThat(resourceService.findComputeResourcePropertyById(storedResource.getId()).getAlgorithm().getId()).isEqualTo(storedAlgo.getId());
    }

    @Test
    void testFindAll() {
        var resourceType = new ComputeResourcePropertyType();
        resourceType.setDescription("Hello World");
        resourceType.setName("Test Name");
        resourceType.setDatatype(ComputeResourcePropertyDataType.FLOAT);

        var resource = new ComputeResourceProperty();
        resource.setComputeResourcePropertyType(resourceType);

        var algo = new QuantumAlgorithm();
        algo.setNisqReady(true);
        algo.setName("MyFirstQuantumAlgorithm");
        algo.setSpeedUp("123");
        algo.setComputationModel(ComputationModel.QUANTUM);
        var storedAlgo = (QuantumAlgorithm) algorithmService.save(algo);

        var storedResource = resourceService.addComputeResourcePropertyToAlgorithm(storedAlgo, resource);

        var byAlgo = resourceService.findAllComputeResourcesPropertyByAlgorithmId(storedAlgo.getId());
        var byAlgoP = resourceService.findAllComputeResourcesPropertyByAlgorithmId(storedAlgo.getId(), Pageable.unpaged());

        assertThat(byAlgo.size()).isEqualTo(1);
        assertThat(byAlgoP.getContent().size()).isEqualTo(1);
    }
}
