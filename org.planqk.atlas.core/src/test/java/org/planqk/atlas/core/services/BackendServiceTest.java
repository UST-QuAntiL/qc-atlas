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

import org.planqk.atlas.core.model.Backend;
import org.planqk.atlas.core.model.BackendProperty;
import org.planqk.atlas.core.model.BackendPropertyType;
import org.planqk.atlas.core.model.QuantumComputationModel;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

public class BackendServiceTest  extends AtlasDatabaseTestBase {

    @Autowired
    private BackendService backendService;

    @Test
    void testAddBackend_WithoutBackendProperties() {
        Backend backend = getGenericTestBackend("testBackend");

        Backend storedBackend = backendService.saveOrUpdate(backend);

        assertBackendEquality(storedBackend, backend);
    }

    @Test
    void testAddBackend_WithBackendProperties() {
        Backend backend = getGenericTestBackend("testBackend");

        Set<BackendProperty> backendProperties = new HashSet<>();
        BackendProperty backendProperty = new BackendProperty();
        // backendProperty.setValue("propertyValue");
        BackendPropertyType backendPropertyType = new BackendPropertyType();
        backendPropertyType.setName("backendPropertyTypeName");
        backendPropertyType.setDescription("backendPropertyTypeDescription");
        backendProperty.setType(backendPropertyType);
        backendProperties.add(backendProperty);
        backend.setBackendProperties(backendProperties);

        Backend storedBackend = backendService.saveOrUpdate(backend);

        assertBackendEquality(storedBackend, backend);
        storedBackend.getBackendProperties().forEach(property -> {
            assertThat(property.getValue()).isEqualTo(backendProperty.getValue());
            assertThat(property.getType().getName()).isEqualTo(backendProperty.getType().getName());
            assertThat(property.getType().getDescription()).isEqualTo(backendProperty.getType().getDescription());
        });
    }

    @Test
    void testUpdateBackend_ElementFound() {
        Backend backend = getGenericTestBackend("testBackend");
        Backend storedBackend = getGenericTestBackend("testBackend");

        Backend storedEditedBackend = backendService.saveOrUpdate(backend);
        storedBackend.setId(storedEditedBackend.getId());
        String editName = "editedBackend";
        storedEditedBackend.setName(editName);
        backendService.saveOrUpdate(storedEditedBackend);

        assertThat(storedEditedBackend.getId()).isNotNull();
        assertThat(storedEditedBackend.getId()).isEqualTo(storedBackend.getId());
        assertThat(storedEditedBackend.getName()).isNotEqualTo(storedBackend.getName());
        assertThat(storedEditedBackend.getQuantumComputationModel()).isEqualTo(storedBackend.getQuantumComputationModel());
        assertThat(storedEditedBackend.getTechnology()).isEqualTo(storedBackend.getTechnology());
        assertThat(storedEditedBackend.getVendor()).isEqualTo(storedBackend.getVendor());
    }

    @Test
    void testFindBackendById_ElementNotFound() {
        Assertions.assertThrows(NoSuchElementException.class, () ->
                backendService.findById(UUID.randomUUID()));
    }

    @Test
    void testFindBackendById_ElementFound() {
        Backend backend = getGenericTestBackend("testBackend");

        Backend storedBackend = backendService.saveOrUpdate(backend);

        storedBackend = backendService.findById(storedBackend.getId());

        assertBackendEquality(storedBackend, backend);
    }

    @Test
    void testFindAll() {
        Set<Backend> backends = new HashSet<>();
        Backend backend1 = getGenericTestBackend("testBackend1");
        Backend backend2 = getGenericTestBackend("testBackend2");
        backends.add(backend1);
        backends.add(backend2);
        backendService.saveOrUpdateAll(backends);

        List<Backend> storedBackends = backendService.findAll(Pageable.unpaged()).getContent();

        assertThat(storedBackends.size()).isEqualTo(2);
    }

    @Test
    void testDeleteBackend_WithoutBackendProperties() {
        Backend backend = getGenericTestBackend("testBackend");

        Backend storedBackend = backendService.saveOrUpdate(backend);

        Assertions.assertDoesNotThrow(() -> backendService.findById(storedBackend.getId()));

        backendService.delete(storedBackend.getId());

        Assertions.assertThrows(NoSuchElementException.class, () ->
                backendService.findById(storedBackend.getId()));
    }

    @Test
    void testDeleteBackend_WithBackendProperties() {
        Backend backend = getGenericTestBackend("testBackend");

        Set<BackendProperty> backendProperties = new HashSet<>();
        BackendProperty backendProperty = new BackendProperty();
        // backendProperty.setValue("propertyValue");
        BackendPropertyType backendPropertyType = new BackendPropertyType();
        backendPropertyType.setName("backendPropertyTypeName");
        backendPropertyType.setDescription("backendPropertyTypeDescription");
        backendProperty.setType(backendPropertyType);
        backendProperties.add(backendProperty);
        backend.setBackendProperties(backendProperties);

        Backend storedBackend = backendService.saveOrUpdate(backend);

        Assertions.assertDoesNotThrow(() -> backendService.findById(storedBackend.getId()));
        storedBackend.getBackendProperties().forEach(property -> {
            assertThat(property.getValue()).isEqualTo(backendProperty.getValue());
            assertThat(property.getType().getName()).isEqualTo(backendProperty.getType().getName());
            assertThat(property.getType().getDescription()).isEqualTo(backendProperty.getType().getDescription());
        });

        backendService.delete(storedBackend.getId());

        Assertions.assertThrows(NoSuchElementException.class, () ->
                backendService.findById(storedBackend.getId()));
    }


    private void assertBackendEquality(Backend dbBackend, Backend compareBackend) {
        assertThat(dbBackend.getId()).isNotNull();
        assertThat(dbBackend.getName()).isEqualTo(compareBackend.getName());
        assertThat(dbBackend.getQuantumComputationModel()).isEqualTo(compareBackend.getQuantumComputationModel());
        assertThat(dbBackend.getTechnology()).isEqualTo(compareBackend.getTechnology());
        assertThat(dbBackend.getVendor()).isEqualTo(compareBackend.getVendor());
    }

    private Backend getGenericTestBackend(String name) {
        Backend backend = new Backend();
        backend.setName(name);
        backend.setQuantumComputationModel(QuantumComputationModel.QUANTUM_ANNEALING);
        backend.setTechnology("testTechnology");
        backend.setVendor("testVendor");
        return backend;
    }
}
