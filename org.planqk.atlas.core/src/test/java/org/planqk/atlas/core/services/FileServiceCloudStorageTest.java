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
import org.planqk.atlas.core.model.File;
import org.planqk.atlas.core.repository.FileRepository;
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
 * Test class for FileServiceCloudStorageImpl.
 * The class is using mock based tests.
 */
@ActiveProfiles({"stoneOne"})
public class FileServiceCloudStorageTest extends AtlasDatabaseTestBase {

    private final String implementationFileBucketName = "planqk-algo-artifacts";

    @Mock
    Blob mockBlob;

    @InjectMocks
    private FileServiceCloudStorageImpl implementationFileServiceCloudStorage;

    @Mock
    private ImplementationRepository implementationRepository;

    @Mock
    private FileRepository fileRepository;

    @Mock
    private Storage storage;

    @Mock
    private Bucket bucket;

    private File file;

    private Implementation persistedImplementation;

    private MultipartFile multipartFile;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        this.file = new File();
        this.file.setId(UUID.randomUUID());
        this.file.setName("name");
        this.file.setMimeType("svg");
        this.file.setFileURL("http://localhost:8080/");
        this.file.setCreationDate(new Date());
        this.file.setLastModifiedAt(new Date());

        this.persistedImplementation = new Implementation();
        this.persistedImplementation.setId(UUID.randomUUID());
        final Set<File> fileSet = new HashSet<>();
        fileSet.add(file);
        this.persistedImplementation.setFiles(fileSet);

        this.multipartFile = this.getMultipartFile();

    }

    @Test
    public void testCreate_success() throws IOException {

        // mock
        BlobId blobId =
                BlobId.of(this.implementationFileBucketName, file.getId() + "/" + this.multipartFile.getOriginalFilename());
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(this.multipartFile.getContentType()).build();

        Mockito.when(this.storage.create(blobInfo, getMultipartFile().getBytes())).thenReturn(this.mockBlob);
        Mockito.when(this.fileRepository.findByFileURL(Mockito.any())).thenReturn(Optional.of(this.file));
        Mockito.when(this.implementationRepository.findById(this.file.getId()))
                .thenReturn(Optional.of(this.persistedImplementation));
        Mockito.when(this.fileRepository.save(Mockito.any())).thenReturn(this.file);

        // call
        final File createdFile =
                implementationFileServiceCloudStorage.create(file.getId(), this.multipartFile);

        // test
        Mockito.verify(this.storage, times(1)).create(blobInfo, getMultipartFile().getBytes());
        Mockito.verify(this.fileRepository, times(1)).save(Mockito.any());
        assertThat(createdFile.getId()).isEqualTo(this.file.getId());
        assertThat(createdFile.getFileURL()).isEqualTo(this.file.getFileURL());
        assertThat(createdFile.getMimeType()).isEqualTo(this.file.getMimeType());
        assertThat(createdFile.getName()).isEqualTo(this.file.getName());

    }

    @Test
    public void testCreate_cloud_storage_exception() throws IOException {

        // mock
        BlobId blobId =
                BlobId.of(this.implementationFileBucketName, file.getId() + "/" + this.multipartFile.getOriginalFilename());
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(this.multipartFile.getContentType()).build();

        Mockito.when(this.storage.create(blobInfo, getMultipartFile().getBytes())).thenThrow(StorageException.class);
        // call + test

        assertThatThrownBy(() -> {
            implementationFileServiceCloudStorage.create(file.getId(), this.multipartFile);
        }).isInstanceOf(CloudStorageException.class)
                .hasMessageContaining("could not create in storage");
    }

    @Test
    public void testFindById() {

        // mock
        Mockito.when(this.fileRepository.findById(this.file.getId()
        )).thenReturn(Optional.of(this.file));

        // call
        final File persistedFile =
                implementationFileServiceCloudStorage.findById(file.getId());

        // test
        Mockito.verify(this.fileRepository, times(1)).findById(this.file.getId());
        assertThat(persistedFile.getId()).isEqualTo(this.file.getId());
        assertThat(persistedFile.getFileURL()).isEqualTo(this.file.getFileURL());
        assertThat(persistedFile.getMimeType()).isEqualTo(this.file.getMimeType());
        assertThat(persistedFile.getName()).isEqualTo(this.file.getName());

    }

    @Test
    public void testFindAllByImplementationId() {
        // mock
        Pageable pageable = PageRequest.of(1, 1);
        Mockito.when(this.fileRepository.findFilesByImplementation_Id(Mockito.any(), Mockito.any()))
                .thenReturn(null);

        // call
        implementationFileServiceCloudStorage.findAllByImplementationId(this.persistedImplementation.getId(), pageable);

        // test
        Mockito.verify(this.fileRepository, times(1)).findFilesByImplementation_Id(Mockito.any(), Mockito.any());
    }

    @Test
    public void testGetImplementationFileContent() {
        // mock
        Mockito.when(this.fileRepository.findById(this.file.getId()))
                .thenReturn(Optional.of(this.file));
        Mockito.when(this.storage.get(implementationFileBucketName))
                .thenReturn(this.bucket);

        Mockito.when(this.bucket.get(this.file.getFileURL()))
                .thenReturn(this.mockBlob);
        // call
        byte[] result = implementationFileServiceCloudStorage.getFileContent(this.file.getId());

        // test
        Mockito.verify(this.fileRepository, times(1)).findById(this.file.getId());
        assertThat(result).isEqualTo(this.mockBlob.getContent());
    }

    @Test
    public void testDelete_success() {

        // mock
        Mockito.when(this.fileRepository.findById(this.file.getId()
        )).thenReturn(Optional.of(this.file));

        // call
        implementationFileServiceCloudStorage.delete(file.getId());

        // test
        Mockito.verify(this.storage, times(1)).delete(Mockito.any(BlobId.class));
        Mockito.verify(this.fileRepository, times(1)).delete(this.file);
    }

    @Test
    public void testDelete_cloud_storage_exception() {

        // mock
        Mockito.when(this.fileRepository.findById(this.file.getId()
        )).thenReturn(Optional.of(this.file));
        Mockito.when(this.storage.delete(Mockito.any(BlobId.class)
        )).thenThrow(StorageException.class);

        // call + test
        assertThatThrownBy(() -> {
            implementationFileServiceCloudStorage.delete(file.getId());
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
