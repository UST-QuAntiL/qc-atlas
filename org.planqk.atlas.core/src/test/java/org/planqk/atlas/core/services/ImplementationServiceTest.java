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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.model.ClassicImplementation;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.QuantumAlgorithm;
import org.planqk.atlas.core.model.QuantumComputationModel;
import org.planqk.atlas.core.model.QuantumImplementation;
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
public class ImplementationServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private ImplementationService implementationService;

    @Autowired
    private AlgorithmService algorithmService;

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
        assertThat(editedImplementation.getLink()).isEqualTo(compareImplementation.getLink());
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

    private Implementation getFullImplementation(String name, Algorithm implementedAlgorithm) {
        Implementation implementation = new ClassicImplementation();

        implementation.setName(name);
        implementation.setImplementedAlgorithm(implementedAlgorithm);
        implementation.setDescription("description");
        implementation.setContributors("contributors");
        implementation.setAssumptions("assumptions");
        implementation.setParameter("parameter");
        implementation.setDependencies("dependencies");
        try {
            implementation.setLink(new URL("http://www.example.com"));
        } catch (MalformedURLException ignored) {}

        return implementation;
    }
}
