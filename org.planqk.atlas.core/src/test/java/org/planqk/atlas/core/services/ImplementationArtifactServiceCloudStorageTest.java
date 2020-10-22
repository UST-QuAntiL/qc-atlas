package org.planqk.atlas.core.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.planqk.atlas.core.exceptions.CloudStorageException;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.ImplementationArtifact;
import org.planqk.atlas.core.repository.ImplementationArtifactRepository;
import org.planqk.atlas.core.repository.ImplementationRepository;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;

/**
 * Test class for ImplementationArtifactServiceCloudStorageImpl.
 * The class is using mock based tests.
 */
@ActiveProfiles({"stoneOne"})
public class ImplementationArtifactServiceCloudStorageTest extends AtlasDatabaseTestBase {

    private final String implementationArtifactsBucketName = "planqk-algo-artifacts";

    @Mock
    Blob mockBlob;

    @InjectMocks
    private ImplementationArtifactServiceCloudStorageImpl implementationArtifactServiceCloudStorage;

    @Mock
    private ImplementationRepository implementationRepository;

    @Mock
    private ImplementationArtifactRepository implementationArtifactRepository;

    @Mock
    private Storage storage;

    @Mock
    private Bucket bucket;

    private ImplementationArtifact implementationArtifact;

    private Implementation persistedImplementation;

    private MultipartFile multipartFile;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        this.implementationArtifact = new ImplementationArtifact();
        this.implementationArtifact.setId(UUID.randomUUID());
        this.implementationArtifact.setName("name");
        this.implementationArtifact.setMimeType("svg");
        this.implementationArtifact.setFileURL("http://localhost:8080/");
        this.implementationArtifact.setCreationDate(new Date());
        this.implementationArtifact.setLastModifiedAt(new Date());

        this.persistedImplementation = new Implementation();
        this.persistedImplementation.setId(UUID.randomUUID());
        final Set<ImplementationArtifact> artifactSet = new HashSet<>();
        artifactSet.add(implementationArtifact);
        this.persistedImplementation.setImplementationArtifacts(artifactSet);

        this.multipartFile = this.getMultipartFile();

    }

    @Test
    public void testCreate_success() throws IOException {

        // mock
        BlobId blobId =
                BlobId.of(this.implementationArtifactsBucketName, implementationArtifact.getId() + "/" + this.multipartFile.getOriginalFilename());
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(this.multipartFile.getContentType()).build();

        Mockito.when(this.storage.create(blobInfo, getMultipartFile().getBytes())).thenReturn(this.mockBlob);
        Mockito.when(this.implementationArtifactRepository.findByFileURL(Mockito.any())).thenReturn(Optional.of(this.implementationArtifact));
        Mockito.when(this.implementationRepository.findById(this.implementationArtifact.getId()))
                .thenReturn(Optional.of(this.persistedImplementation));
        Mockito.when(this.implementationArtifactRepository.save(Mockito.any())).thenReturn(this.implementationArtifact);

        // call
        final ImplementationArtifact createdImplementationArtifact =
                implementationArtifactServiceCloudStorage.create(implementationArtifact.getId(), this.multipartFile);

        // test
        Mockito.verify(this.storage, times(1)).create(blobInfo, getMultipartFile().getBytes());
        Mockito.verify(this.implementationArtifactRepository, times(1)).save(Mockito.any());
        assertThat(createdImplementationArtifact.getId()).isEqualTo(this.implementationArtifact.getId());
        assertThat(createdImplementationArtifact.getFileURL()).isEqualTo(this.implementationArtifact.getFileURL());
        assertThat(createdImplementationArtifact.getMimeType()).isEqualTo(this.implementationArtifact.getMimeType());
        assertThat(createdImplementationArtifact.getName()).isEqualTo(this.implementationArtifact.getName());

    }

    @Test
    public void testCreate_cloud_storage_exception() throws IOException {

        // mock
        BlobId blobId =
                BlobId.of(this.implementationArtifactsBucketName, implementationArtifact.getId() + "/" + this.multipartFile.getOriginalFilename());
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(this.multipartFile.getContentType()).build();

        Mockito.when(this.storage.create(blobInfo, getMultipartFile().getBytes())).thenThrow(StorageException.class);
        // call + test

        assertThatThrownBy(() -> {
            implementationArtifactServiceCloudStorage.create(implementationArtifact.getId(), this.multipartFile);
        }).isInstanceOf(CloudStorageException.class)
                .hasMessageContaining("could not create in storage");
    }

    @Test
    public void testFindById() {

        // mock
        Mockito.when(this.implementationArtifactRepository.findById(this.implementationArtifact.getId()
        )).thenReturn(Optional.of(this.implementationArtifact));

        // call
        final ImplementationArtifact persistedArtifact =
                implementationArtifactServiceCloudStorage.findById(implementationArtifact.getId());

        // test
        Mockito.verify(this.implementationArtifactRepository, times(1)).findById(this.implementationArtifact.getId());
        assertThat(persistedArtifact.getId()).isEqualTo(this.implementationArtifact.getId());
        assertThat(persistedArtifact.getFileURL()).isEqualTo(this.implementationArtifact.getFileURL());
        assertThat(persistedArtifact.getMimeType()).isEqualTo(this.implementationArtifact.getMimeType());
        assertThat(persistedArtifact.getName()).isEqualTo(this.implementationArtifact.getName());

    }

    @Test
    public void testFindAllByImplementationId() {
        // mock
        Pageable pageable = PageRequest.of(1, 1);
        Mockito.when(this.implementationArtifactRepository.findImplementationArtifactsByImplementation_Id(Mockito.any(), Mockito.any()))
                .thenReturn(null);

        // call
        implementationArtifactServiceCloudStorage.findAllByImplementationId(this.persistedImplementation.getId(), pageable);

        // test
        Mockito.verify(this.implementationArtifactRepository, times(1)).findImplementationArtifactsByImplementation_Id(Mockito.any(), Mockito.any());
    }

    @Test
    public void testGetImplementationArtifactContent() {
        // mock
        Mockito.when(this.implementationArtifactRepository.findById(this.implementationArtifact.getId()))
                .thenReturn(Optional.of(this.implementationArtifact));
        Mockito.when(this.storage.get(implementationArtifactsBucketName))
                .thenReturn(this.bucket);

        Mockito.when(this.bucket.get(this.implementationArtifact.getFileURL()))
                .thenReturn(this.mockBlob);
        // call
        byte[] result = implementationArtifactServiceCloudStorage.getImplementationArtifactContent(this.implementationArtifact.getId());

        // test
        Mockito.verify(this.implementationArtifactRepository, times(1)).findById(this.implementationArtifact.getId());
        assertThat(result).isEqualTo(this.mockBlob.getContent());
    }

    @Test
    public void testDelete_success() {

        // mock
        Mockito.when(this.implementationArtifactRepository.findById(this.implementationArtifact.getId()
        )).thenReturn(Optional.of(this.implementationArtifact));

        // call
        implementationArtifactServiceCloudStorage.delete(implementationArtifact.getId());

        // test
        Mockito.verify(this.storage, times(1)).delete(Mockito.any(BlobId.class));
        Mockito.verify(this.implementationArtifactRepository, times(1)).delete(this.implementationArtifact);
    }

    @Test
    public void testDelete_cloud_storage_exception() {

        // mock
        Mockito.when(this.implementationArtifactRepository.findById(this.implementationArtifact.getId()
        )).thenReturn(Optional.of(this.implementationArtifact));
        Mockito.when(this.storage.delete(Mockito.any(BlobId.class)
        )).thenThrow(StorageException.class);

        // call + test
        assertThatThrownBy(() -> {
            implementationArtifactServiceCloudStorage.delete(implementationArtifact.getId());
        }).isInstanceOf(CloudStorageException.class)
                .hasMessageContaining("could not delete from storage");
    }

    private MultipartFile getMultipartFile() {
        String name = "file.txt";
        String originalFileName = "file.txt";
        String contentType = "text/plain";
        byte[] content = null;
        return new MockMultipartFile(name,
                originalFileName, contentType, content);
    }
}
