package org.planqk.atlas.core.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.planqk.atlas.core.exceptions.CloudStorageException;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.ImplementationArtifact;
import org.planqk.atlas.core.repository.ImplementationArtifactRepository;
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

@ActiveProfiles({"test", "stoneOne"})
public class ImplementationArtifactServiceCloudStorageIntegrationTest extends AtlasDatabaseTestBase {

    @Autowired
    ImplementationArtifactService implementationArtifactServiceCloudStorage;

    @Autowired
    Storage storage;

    @Autowired
    private ImplementationArtifactRepository implementationArtifactRepository;

    @Autowired
    private ImplementationRepository implementationRepository;

    @Mock
    Blob mockBlob;

    @Test
    public void givenImplementationArtifactNotExists_WhenCreate_ThenShouldBeCreatedAndLinkedToImplementation() {
        // Given
        when(storage.create(Mockito.any(BlobInfo.class), Mockito.any(byte[].class))).thenReturn(mockBlob);
        Implementation persistedImplementation = implementationRepository.save(getDummyImplementation());
        assertThat(implementationArtifactRepository.findAll().size()).isEqualTo(0);

        //When
        ImplementationArtifact createdImplementationArtifact =
            implementationArtifactServiceCloudStorage.create(persistedImplementation.getId(), getMultipartFile());

        //Then
        assertThat(implementationArtifactRepository.findAll().size()).isEqualTo(1);
        assertThat(implementationArtifactRepository.findById(createdImplementationArtifact.getId())).isPresent();
        assertThat(createdImplementationArtifact.getImplementation().getId()).isEqualTo(persistedImplementation.getId());
    }

    @Test
    public void givenNone_WhenCreateAndStorageExceptionIsThrown_ThenCatchAndThrowCloudStorageException() {
        // Given
        Implementation persistedImplementation = implementationRepository.save(getDummyImplementation());
        when(storage.create(Mockito.any(BlobInfo.class), Mockito.any(byte[].class))).thenThrow(StorageException.class);

        // When
        Assertions.assertThrows(CloudStorageException.class,
            () -> implementationArtifactServiceCloudStorage.create(persistedImplementation.getId(), getMultipartFile()));
    }

    @Test
    public void givenImplementationArtifactExists_whenFindById_ThenShouldReturnImplementationArtifact() {
        // Given
        ImplementationArtifact persistedImplementationArtifact = implementationArtifactRepository.save(getDummyImplementationArtifact());
        // When Then
        assertThat(implementationArtifactServiceCloudStorage.findById(persistedImplementationArtifact.getId()))
            .isEqualToComparingFieldByField(persistedImplementationArtifact);
    }

    @Test
    public void givenImplementationArtifactsOfImplementationExists_whenFindAllByImplementationId_thenShouldReturnAllImplementationArtifactsOfImpl() {
        // Given
        Implementation persistedImplementation = implementationRepository.save(getDummyImplementation());
        ImplementationArtifact implementationArtifact = getDummyImplementationArtifact();
        ImplementationArtifact implementationArtifactTwo = getDummyImplementationArtifact();
        implementationArtifact.setImplementation(persistedImplementation);
        implementationArtifactTwo.setImplementation(persistedImplementation);
        implementationArtifactRepository.save(implementationArtifact);
        implementationArtifactRepository.save(implementationArtifactTwo);

        // When
        Page<ImplementationArtifact> implementationArtifacts =
            implementationArtifactServiceCloudStorage.findAllByImplementationId(persistedImplementation.getId(), PageRequest.of(1, 10));

        // Then
        assertThat(implementationArtifacts.getTotalElements()).isEqualTo(2);
    }

    @Test
    public void delete() {
        // Given
        ImplementationArtifact persistedImplementationArtifact = implementationArtifactRepository.save(getDummyImplementationArtifact());

        // When
        when(storage.delete(Mockito.any(BlobId.class))).thenReturn(true);
        implementationArtifactServiceCloudStorage.delete(persistedImplementationArtifact.getId());

        //Then
        assertThat(implementationArtifactRepository.findById(persistedImplementationArtifact.getId())).isNotPresent();
    }

    private ImplementationArtifact getDummyImplementationArtifact() {
        ImplementationArtifact implementationArtifact = new ImplementationArtifact();
        implementationArtifact.setName("Test");
        implementationArtifact.setFileURL("implId/artifactId" + Math.random());
        return implementationArtifact;
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
