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

import java.util.NoSuchElementException;
import java.util.Random;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.planqk.atlas.core.exceptions.CloudStorageException;
import org.planqk.atlas.core.model.File;
import org.planqk.atlas.core.model.FileImplementationPackage;
import org.planqk.atlas.core.model.ImplementationPackage;
import org.planqk.atlas.core.model.ImplementationPackageType;
import org.planqk.atlas.core.repository.FileRepository;
import org.planqk.atlas.core.repository.ImplementationPackageRepository;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;

@ActiveProfiles({"test", "google-cloud"})
public class FileServiceCloudStorageTest extends AtlasDatabaseTestBase {

    @Autowired
    private FileService fileServiceCloudStorage;

    @Autowired
    private Storage storage;

    @Mock
    private Blob mockBlob;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private ImplementationPackageRepository implementationPackageRepository;

    @Test
    public void givenFileNotExists_WhenCreate_ThenShouldBeCreated() {
        // Given
        when(storage.create(Mockito.any(BlobInfo.class), Mockito.any(byte[].class))).thenReturn(mockBlob);
        ImplementationPackage persistedImplementationPackage = implementationPackageRepository.save(getDummyImplementationPackage());
        assertThat(fileRepository.findAll().size()).isEqualTo(0);

        //When
        File createdFile =
                fileServiceCloudStorage.create(getMultipartFile());

        //Then
        assertThat(fileRepository.findAll().size()).isEqualTo(1);
        assertThat(fileRepository.findById(createdFile.getId())).isPresent();
    }

    @Test
    public void givenNone_WhenCreateAndStorageExceptionIsThrown_ThenCatchAndThrowCloudStorageException() {
        // Given
        ImplementationPackage persistedImplementationPackage = implementationPackageRepository.save(getDummyImplementationPackage());
        when(storage.create(Mockito.any(BlobInfo.class), Mockito.any(byte[].class))).thenThrow(StorageException.class);

        // When
        Assertions.assertThrows(CloudStorageException.class,
                () -> fileServiceCloudStorage.create(getMultipartFile()));
    }

    @Test
    public void givenFileExists_whenFindById_ThenShouldReturnFile() {
        // Given
        File persistedFile = fileRepository.save(getDummyFile());
        // When Then
        assertThat(fileServiceCloudStorage.findById(persistedFile.getId()))
                .isEqualToComparingFieldByField(persistedFile);
    }

    @Test
    public void delete_success() {
        // Given
        File persistedFile = fileRepository.save(getDummyFile());

        // When
        when(storage.delete(Mockito.any(BlobId.class))).thenReturn(true);
        fileServiceCloudStorage.delete(persistedFile.getId());

        //Then
        assertThat(fileRepository.findById(persistedFile.getId())).isNotPresent();
    }

    @Test
    public void delete_cloudStorageExceptionWasThrown() {
        // Given
        File persistedFile = fileRepository.save(getDummyFile());

        // When
        when(storage.delete(Mockito.any(BlobId.class))).thenThrow(StorageException.class);

        // Call + Then
        Assertions.assertThrows(CloudStorageException.class,
                () -> fileServiceCloudStorage.delete(persistedFile.getId()));
    }

    @Test
    public void getFileContent_success() {
        // Given
        File persistedFile = fileRepository.save(getDummyFile());

        // When
        when(storage.get(Mockito.any(BlobId.class))).thenReturn(this.mockBlob);
        byte[] result = fileServiceCloudStorage.getFileContent(persistedFile.getId());

        //Then
        assertThat(result).isEqualTo(this.mockBlob.getContent());
    }

    @Test
    public void getFileContent_noSuchElementExceptionWasThrown() {
        // Given
        File persistedFile = fileRepository.save(getDummyFile());

        // Call + Then
        Assertions.assertThrows(NoSuchElementException.class,
                () -> fileServiceCloudStorage.getFileContent(persistedFile.getId()));
    }

    @Test
    public void getFileContent_cloudStorageExceptionWasThrown() {
        // Given
        File persistedFile = fileRepository.save(getDummyFile());

        //when
        when(storage.get(Mockito.any(BlobId.class))).thenThrow(StorageException.class);

        // Call + Then
        Assertions.assertThrows(CloudStorageException.class,
                () -> fileServiceCloudStorage.getFileContent(persistedFile.getId()));
    }

    private File getDummyFile() {
        File file = new File();
        file.setName("Test");
        file.setFileURL("implId/fileId" + Math.random());
        return file;
    }

    private MultipartFile getMultipartFile() {
        String name = "file.txt";
        String originalFileName = "file.txt";
        String contentType = "text/plain";
        byte[] content = generateRandomByteArray();
        return new MockMultipartFile(name,
                originalFileName, contentType, content);
    }

    private ImplementationPackage getDummyImplementationPackage() {
        ImplementationPackage dummyImplementationPackage = new FileImplementationPackage();
        dummyImplementationPackage.setId(UUID.randomUUID());
        dummyImplementationPackage.setName("dummy ImplPackage");
        dummyImplementationPackage.setPackageType(ImplementationPackageType.FILE);
        dummyImplementationPackage.setFile(null);
        return dummyImplementationPackage;
    }

    private byte[] generateRandomByteArray() {
        Random rd = new Random();
        byte[] arr = new byte[7];
        rd.nextBytes(arr);
        return arr;
    }
}
