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

import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.QuantumAlgorithm;
import org.planqk.atlas.core.model.QuantumResource;
import org.planqk.atlas.core.model.QuantumResourceDataType;
import org.planqk.atlas.core.model.QuantumResourceType;
import org.planqk.atlas.core.repository.QuantumResourceRepository;
import org.planqk.atlas.core.repository.QuantumResourceTypeRepository;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QuantumResourceTest extends AtlasDatabaseTestBase {

    @Autowired
    private QuantumResourceService resourceService;
    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private QuantumResourceTypeRepository typeRepository;
    @Autowired
    private QuantumResourceRepository resourceRepository;

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
        assertTrue(resourceOpt.isPresent());
        var resultResource = resourceOpt.get();
        assertNull(resultResource.getAlgorithm());
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
}
