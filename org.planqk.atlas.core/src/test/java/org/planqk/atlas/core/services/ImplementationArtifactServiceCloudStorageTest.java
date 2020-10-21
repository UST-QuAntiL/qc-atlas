package org.planqk.atlas.core.services;

import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.repository.ImplementationArtifactRepository;
import org.planqk.atlas.core.repository.ImplementationRepository;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;


@ActiveProfiles({"stoneOne"})
public class ImplementationArtifactServiceCloudStorageTest extends AtlasDatabaseTestBase {

    private ImplementationArtifactService implementationArtifactServiceCloudStorage;

    @Autowired
    private ImplementationRepository implementationRepository;

    @Autowired
    private ImplementationArtifactRepository implementationArtifactRepository;

    @Mock
    private Storage storage;


    @Test
    void givenImplementationArtifactNotExists_whenCreate_thenImplementationArtifactShouldBeCreated() {
        implementationArtifactServiceCloudStorage =
                new ImplementationArtifactServiceCloudStorageImpl(storage, implementationArtifactRepository, implementationRepository);
        Blob blobMock = mock(Blob.class);
        //@TODO: Mock here :)
        Mockito.when(storage.create(Mockito.any(BlobInfo.class), Mockito.any())).thenReturn(blobMock);
        Implementation persistedImplementation = implementationRepository.save(this.getDummyImplementation());
        implementationArtifactServiceCloudStorage.create(persistedImplementation.getId(), getDummyMultipartFile());
    }

//    @Test
//    void givenImplementationArtifactNotExists_whenCreate_thenImplementationArtifactShouldBeCreated() {
//        Blob blobMock = mock(Blob.class);
//        Mockito.when(storage.create(Mockito.any(BlobInfo.class), Mockito.any())).thenReturn(blobMock);
//        Implementation persistedImplementation = implementationRepository.save(this.getDummyImplementation());
//        implementationArtifactServiceCloudStorage.create(persistedImplementation.getId(), getDummyMultipartFile());
//    }

    private Implementation getDummyImplementation() {
        Implementation implementation = new Implementation();
        implementation.setName("Dummy Impl");
        return implementation;
    }

    private MultipartFile getDummyMultipartFile() {
        String name = "file.txt";
        String originalFileName = "file.txt";
        String contentType = "text/plain";
        byte[] content = null;
        return new MockMultipartFile(name,
                originalFileName, contentType, content);
    }
}
