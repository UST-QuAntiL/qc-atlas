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
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.File;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.ImplementationPackage;
import org.planqk.atlas.core.model.ImplementationPackageType;
import org.planqk.atlas.core.repository.FileRepository;
import org.planqk.atlas.core.repository.ImplementationPackageRepository;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.storage.BlobInfo;

public class FileServiceTest extends AtlasDatabaseTestBase {

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
        var algo = new Algorithm();
        algo.setName("TestAlgo");
        algo  = algorithmService.create(algo);

        var impl = new Implementation();
        impl.setName("TestImpl");
        impl = implementationService.create(impl, algo.getId());

        implementation = impl;

        implementationPackage = new ImplementationPackage();
        implementationPackage.setName("Name");
        implementationPackage.setDescription("Description");
        implementationPackage.setPackageType(ImplementationPackageType.FILE);
        implementationPackage.setImplementation(implementation);
        implementationPackage = implementationPackageService.create(implementationPackage, implementation.getId());

        MultipartFile file = new MockMultipartFile("file.txt", "file.txt", "text/plain", new byte[0]);
        multipartFile = file;
    }

    @Test
    public void createFile() {
        File file = fileService.create(implementationPackage.getId(), multipartFile);
        assertThat(fileRepository.findAll().size()).isEqualTo(1);
    }
}
