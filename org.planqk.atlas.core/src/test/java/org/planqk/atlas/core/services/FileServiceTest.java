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

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.File;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.ImplementationPackage;
import org.planqk.atlas.core.model.ImplementationPackageType;
import org.planqk.atlas.core.repository.FileRepository;
import org.planqk.atlas.core.repository.ImplementationPackageRepository;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

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
        algo = algorithmService.create(algo);

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
        assertThat(new java.io.File(file.getFileURL()).isFile()).isTrue();
        assertThat(new java.io.File(file.getFileURL()).exists()).isTrue();
        assertThat(fileRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    public void findFileById() {
        var storedFile = fileService.create(implementationPackage.getId(), multipartFile);

        var foundFile = fileService.findById(storedFile.getId());

        assertThat(storedFile.getId()).isEqualTo(foundFile.getId());
    }

    @Test
    void findFile_ElementNotFound() {
        assertThrows(NoSuchElementException.class, () -> {
            fileService.findById(UUID.randomUUID());
        });
    }

    @Test
    void deleteFile_ElementFound() {
        File persistedFile = fileService.create(implementationPackage.getId(), multipartFile);
        fileService.delete(persistedFile.getId());
        assertThat(fileRepository.findById(persistedFile.getId())).isNotPresent();
    }

    @Test
    public void getFileContent() {
        String name = "file.txt";
        String originalFileName = "file.txt";
        String contentType = "text/plain";
        byte[] content = generateRandomByteArray();

        MultipartFile multipartFileWithContent = new MockMultipartFile(name,
            originalFileName, contentType, content);
        File persistedFile = fileService.create(implementationPackage.getId(), multipartFileWithContent);
        byte[] result = fileService.getFileContent(persistedFile.getId());

        assertThat(result).isEqualTo(content);
    }

    private byte[] generateRandomByteArray() {
        Random rd = new Random();
        byte[] arr = new byte[7];
        rd.nextBytes(arr);
        return arr;
    }
}
