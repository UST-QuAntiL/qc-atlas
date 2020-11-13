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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.model.DiscussionTopic;
import org.planqk.atlas.core.model.File;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.ImplementationPackage;
import org.planqk.atlas.core.model.ImplementationPackageType;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.repository.FileRepository;
import org.planqk.atlas.core.repository.ImplementationPackageRepository;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.planqk.atlas.core.util.ServiceTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class ImplementationPackageServiceTest extends AtlasDatabaseTestBase {

    private final int page = 0;

    private final int size = 2;

    private final Pageable pageable = PageRequest.of(page, size);

    @Autowired
    private FileService fileService;

    @Autowired
    private ImplementationService implementationService;

    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private ImplementationPackageService implementationPackageService;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private ImplementationPackageRepository implementationPackageRepository;

    private ImplementationPackage implementationPackage;

    private ImplementationPackage implementationPackage1;

    private Implementation implementation;

    private MultipartFile multipartFile;

    @BeforeEach
    public void initialize() {
        implementationPackage = new ImplementationPackage();
        implementationPackage.setName("Name");
        implementationPackage.setDescription("Description");
        implementationPackage.setPackageType(ImplementationPackageType.FILE);

        implementationPackage1 = new ImplementationPackage();
        implementationPackage1.setName("Name1");
        implementationPackage1.setDescription("Description1");
        implementationPackage1.setPackageType(ImplementationPackageType.TOSCA);

        var algo = new Algorithm();
        algo.setName("TestAlgo");
        algo  = algorithmService.create(algo);

        var impl = new Implementation();
        impl.setName("TestImpl");
        impl = implementationService.create(impl, algo.getId());

        implementationPackage.setImplementation(impl);
        implementation = impl;

        MultipartFile file = new MockMultipartFile("file.txt", "file.txt", "text/plain", new byte[0]);
        multipartFile = file;
    }

    @Test
    public void createImplementationPackage() {
        ImplementationPackage implementationPackage = implementationPackageService.create(this.implementationPackage, implementation.getId());
        assertThat(implementationPackage.getId()).isNotNull();
        assertThat(implementationPackage.getName()).isEqualTo(this.implementationPackage.getName());
        assertThat(implementationPackage.getDescription()).isEqualTo(this.implementationPackage.getDescription());
        assertThat(implementationPackage.getPackageType()).isEqualTo(this.implementationPackage.getPackageType());
        assertThat(implementationPackage.getImplementation()).isEqualTo(this.implementationPackage.getImplementation());
    }

    @Test
    public void findAllImplementationPackages() {
        implementationPackageService.create(implementationPackage, implementation.getId());
        implementationPackageService.create(implementationPackage1, implementation.getId());

        Page<ImplementationPackage> implementationPackages = implementationPackageService.findImplementationPackagesByImplementationId(implementation.getId(), pageable);
        assertThat(implementationPackages.getTotalElements()).isEqualTo(2);
    }

    @Test
    void findImplementationPackageById_ElementNotFound() {
        assertThrows(NoSuchElementException.class, () -> {
            implementationPackageService.findById(UUID.randomUUID());
        });
    }

    @Test
    void findImplementationPackageById_ElementFound() {
        var storedImplementationPackage = implementationPackageService.create(implementationPackage, implementation.getId());

        var foundImplementationPackage = implementationPackageService.findById(implementationPackage.getId());

        assertThat(storedImplementationPackage.getId()).isEqualTo(foundImplementationPackage.getId());
    }

    @Test
    void updateImplementationPackage_ElementFound() {
        ImplementationPackage implementationPackage = implementationPackageService.create(this.implementationPackage, implementation.getId());
        implementationPackage.setName("New Title");
        ImplementationPackage update = implementationPackageService.update(implementationPackage);

        assertThat(update.getName()).isEqualTo(implementationPackage.getName());
        assertThat(update.getDescription()).isEqualTo(implementationPackage.getDescription());
        assertThat(update.getPackageType()).isEqualTo(implementationPackage.getPackageType());
        assertThat(implementationPackage.getImplementation()).isEqualTo(this.implementationPackage.getImplementation());
    }

    @Test
    void updateImplementationPackage_ElementNotFound() {
        implementationPackage.setId(UUID.randomUUID());
        assertThrows(NoSuchElementException.class, () -> {
            implementationPackageService.update(this.implementationPackage);
        });
    }

    @Test
    void deleteImplementationPackage_ElementFound() {
        ImplementationPackage implementationPackage = implementationPackageService.create(this.implementationPackage, implementation.getId());
        implementationPackageService.delete(implementationPackage.getId());
        assertThrows(NoSuchElementException.class, () -> {
            implementationPackageService.findById(implementationPackage.getId());
        });
    }

    @Test
    void checkIfImplementationPackageIsLinkedToImplementation() {
        var algo = new Algorithm();
        algo.setName("TestAlgo");
        algo  = algorithmService.create(algo);

        var impl = new Implementation();
        impl.setName("Impl");
        impl = implementationService.create(impl, algo.getId());

        implementationPackage.setImplementation(impl);
        implementationPackage = implementationPackageService.create(implementationPackage, impl.getId());
        implementationPackageService.checkIfImplementationPackageIsLinkedToImplementation(implementationPackage.getId(), impl.getId());

        Algorithm finalAlgo = algo;
        assertThrows(NoSuchElementException.class, () -> {
            var impl2 = new Implementation();
            impl2.setName("Impl2");
            impl2 = implementationService.create(impl2, finalAlgo.getId());
            implementationPackageService.checkIfImplementationPackageIsLinkedToImplementation(implementationPackage.getId(), impl2.getId());
        });
    }
}
