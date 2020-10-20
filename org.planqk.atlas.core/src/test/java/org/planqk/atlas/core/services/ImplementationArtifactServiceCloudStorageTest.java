package org.planqk.atlas.core.services;

import org.junit.jupiter.api.Test;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.repository.ImplementationRepository;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.contrib.nio.testing.LocalStorageHelper;

@ActiveProfiles({"test", "stoneOne"})
public class ImplementationArtifactServiceCloudStorageTest extends AtlasDatabaseTestBase {

    @Autowired
    private ImplementationArtifactService implementationArtifactServiceCloudStorage;

    @Autowired
    private ImplementationRepository implementationRepository;

    @Test
    void givenImplementationArtifactNotExists_whenCreate_thenImplementationArtifactShouldBeCreated() {

        Implementation persistedImplementation = implementationRepository.save(this.getDummyImplementation());
        implementationArtifactServiceCloudStorage.create(persistedImplementation.getId(), getDummyMultipartFile());
    }

    private Implementation getDummyImplementation(){
        Implementation implementation = new Implementation();
        implementation.setName("Dummy Impl");
        return implementation;
    }

    private MultipartFile getDummyMultipartFile(){
        String name = "file.txt";
        String originalFileName = "file.txt";
        String contentType = "text/plain";
        byte[] content = null;
        return new MockMultipartFile(name,
                originalFileName, contentType, content);
    }
}
