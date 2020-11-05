package org.planqk.atlas.core.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.NoSuchElementException;
import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.planqk.atlas.core.exceptions.CloudStorageException;
import org.planqk.atlas.core.model.File;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.repository.FileRepository;
import org.planqk.atlas.core.repository.ImplementationRepository;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private ImplementationRepository implementationRepository;

    @Test
    public void givenFileNotExists_WhenCreate_ThenShouldBeCreated() {
        // Given
        when(storage.create(Mockito.any(BlobInfo.class), Mockito.any(byte[].class))).thenReturn(mockBlob);
        Implementation persistedImplementation = implementationRepository.save(getDummyImplementation());
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
        Implementation persistedImplementation = implementationRepository.save(getDummyImplementation());
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

    private Implementation getDummyImplementation() {
        Implementation dummyImplementation = new Implementation();
        dummyImplementation.setName("dummy Impl");
        return dummyImplementation;
    }

    private byte[] generateRandomByteArray() {
        Random rd = new Random();
        byte[] arr = new byte[7];
        rd.nextBytes(arr);
        return arr;
    }

}
