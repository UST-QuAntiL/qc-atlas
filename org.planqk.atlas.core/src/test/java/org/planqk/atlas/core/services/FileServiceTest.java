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
import java.util.Random;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.planqk.atlas.core.model.File;
import org.planqk.atlas.core.repository.FileRepository;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class FileServiceTest extends AtlasDatabaseTestBase {

    private final int page = 0;

    private final int size = 2;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileRepository fileRepository;

    private MultipartFile multipartFile;

    @BeforeEach
    public void initialize() {
        MultipartFile file = new MockMultipartFile("file.txt", "file.txt", "text/plain", new byte[0]);
        multipartFile = file;
    }

    @Test
    public void createFile() {
        File file = fileService.create(multipartFile);
        assertThat(new java.io.File(file.getFileURL()).isFile()).isTrue();
        assertThat(new java.io.File(file.getFileURL()).exists()).isTrue();
        assertThat(fileRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    public void findFileById() {
        var storedFile = fileService.create(multipartFile);

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
        UUID persistedFileId = fileService.create(multipartFile).getId();
        fileService.delete(persistedFileId);
        assertThat(fileRepository.findById(persistedFileId)).isNotPresent();
    }

    @Test
    public void getFileContent() {
        String name = "file.txt";
        String originalFileName = "file.txt";
        String contentType = "text/plain";
        byte[] content = generateRandomByteArray();

        MultipartFile multipartFileWithContent = new MockMultipartFile(name,
                originalFileName, contentType, content);
        File persistedFile = fileService.create(multipartFileWithContent);
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
