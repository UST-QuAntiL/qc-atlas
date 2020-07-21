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

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.ComputeResource;
import org.planqk.atlas.core.model.QuantumComputationModel;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

public class ComputeResourceServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private ComputeResourceService computeResourceService;

    @Test
    void testAddComputeResource_WithoutComputeResourceProperties() {
        ComputeResource computeResource = getGenericTestComputeResource("testComputeResource");

        ComputeResource storedComputeResource = computeResourceService.saveOrUpdate(computeResource);

        assertComputeResourceEquality(storedComputeResource, computeResource);
    }

    @Test
    void testUpdateComputeResource_ElementFound() {
        ComputeResource computeResource = getGenericTestComputeResource("testComputeResource");
        ComputeResource storedComputeResource = getGenericTestComputeResource("testComputeResource");

        ComputeResource storedEditedComputeResource = computeResourceService.saveOrUpdate(computeResource);
        storedComputeResource.setId(storedEditedComputeResource.getId());
        String editName = "editedComputeResource";
        storedEditedComputeResource.setName(editName);
        computeResourceService.saveOrUpdate(storedEditedComputeResource);

        assertThat(storedEditedComputeResource.getId()).isNotNull();
        assertThat(storedEditedComputeResource.getId()).isEqualTo(storedComputeResource.getId());
        assertThat(storedEditedComputeResource.getName()).isNotEqualTo(storedComputeResource.getName());
        assertThat(storedEditedComputeResource.getQuantumComputationModel()).isEqualTo(storedComputeResource.getQuantumComputationModel());
        assertThat(storedEditedComputeResource.getTechnology()).isEqualTo(storedComputeResource.getTechnology());
        assertThat(storedEditedComputeResource.getVendor()).isEqualTo(storedComputeResource.getVendor());
    }

    @Test
    void testFindComputeResourceById_ElementNotFound() {
        Assertions.assertThrows(NoSuchElementException.class, () ->
                computeResourceService.findById(UUID.randomUUID()));
    }

    @Test
    void testFindComputeResourceById_ElementFound() {
        ComputeResource computeResource = getGenericTestComputeResource("testComputeResource");

        ComputeResource storedComputeResource = computeResourceService.saveOrUpdate(computeResource);

        storedComputeResource = computeResourceService.findById(storedComputeResource.getId());

        assertComputeResourceEquality(storedComputeResource, computeResource);
    }

    @Test
    void testFindAll() {
        Set<ComputeResource> computeResources = new HashSet<>();
        ComputeResource computeResource1 = getGenericTestComputeResource("testComputeResource1");
        ComputeResource computeResource2 = getGenericTestComputeResource("testComputeResource2");
        computeResources.add(computeResource1);
        computeResources.add(computeResource2);
        computeResourceService.saveOrUpdateAll(computeResources);

        List<ComputeResource> storedComputeResources = computeResourceService.findAll(Pageable.unpaged()).getContent();

        assertThat(storedComputeResources.size()).isEqualTo(2);
    }

    @Test
    void testDeleteComputeResource_WithoutComputeResourceProperties() {
        ComputeResource computeResource = getGenericTestComputeResource("testComputeResource");

        ComputeResource storedComputeResource = computeResourceService.saveOrUpdate(computeResource);

        Assertions.assertDoesNotThrow(() -> computeResourceService.findById(storedComputeResource.getId()));

        computeResourceService.delete(storedComputeResource.getId());

        Assertions.assertThrows(NoSuchElementException.class, () ->
                computeResourceService.findById(storedComputeResource.getId()));
    }

    private void assertComputeResourceEquality(ComputeResource dbComputeResource, ComputeResource compareComputeResource) {
        assertThat(dbComputeResource.getId()).isNotNull();
        assertThat(dbComputeResource.getName()).isEqualTo(compareComputeResource.getName());
        assertThat(dbComputeResource.getQuantumComputationModel()).isEqualTo(compareComputeResource.getQuantumComputationModel());
        assertThat(dbComputeResource.getTechnology()).isEqualTo(compareComputeResource.getTechnology());
        assertThat(dbComputeResource.getVendor()).isEqualTo(compareComputeResource.getVendor());
    }

    private ComputeResource getGenericTestComputeResource(String name) {
        ComputeResource computeResource = new ComputeResource();
        computeResource.setName(name);
        computeResource.setQuantumComputationModel(QuantumComputationModel.QUANTUM_ANNEALING);
        computeResource.setTechnology("testTechnology");
        computeResource.setVendor("testVendor");
        return computeResource;
    }
}
