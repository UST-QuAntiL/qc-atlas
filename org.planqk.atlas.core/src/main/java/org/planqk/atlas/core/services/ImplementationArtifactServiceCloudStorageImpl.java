package org.planqk.atlas.core.services;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.ImplementationArtifact;
import org.planqk.atlas.core.repository.ImplementationArtifactRepository;
import org.planqk.atlas.core.repository.ImplementationRepository;
import org.planqk.atlas.core.util.ServiceUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import lombok.AllArgsConstructor;

@Service
@Profile("stoneOne")
@AllArgsConstructor
public class ImplementationArtifactServiceCloudStorageImpl implements ImplementationArtifactService {

    private final Storage storage = StorageOptions.getDefaultInstance().getService();

    private final String implementationArtifactsBucketName = "planqk-algo-artifacts";

    private final ImplementationArtifactRepository implementationArtifactRepository;

    private final ImplementationRepository implementationRepository;

    @Override
    public ImplementationArtifact create(UUID implementationId, MultipartFile file) {
        Bucket bucket = storage.get(implementationArtifactsBucketName);
        Blob blob = null;
        try {
            blob = bucket.create(implementationId + "/" + file.getOriginalFilename(), file.getBytes(), file.getContentType());
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImplementationArtifact implementationArtifact = getImplementationArtifactFromBlob(blob);
        Optional<ImplementationArtifact> persistedImplementationArtifactOptional = implementationArtifactRepository.findByFileURL(implementationArtifact.getFileURL());
        if(persistedImplementationArtifactOptional.isPresent()){
            implementationArtifact.setId(persistedImplementationArtifactOptional.get().getId());
        }
        Implementation implementation = ServiceUtils.findById(implementationId, Implementation.class, implementationRepository);
        implementationArtifact.setImplementation(implementation);
        return implementationArtifactRepository.save(implementationArtifact);
    }

    @Override
    public ImplementationArtifact findById(UUID id) {
        return ServiceUtils.findById(id, ImplementationArtifact.class, implementationArtifactRepository);
    }

    @Override
    public Collection<ImplementationArtifact> findAllByImplementationId(UUID implementationId) {
        Implementation implementation = ServiceUtils.findById(implementationId, Implementation.class, implementationRepository);
        return implementation.getImplementationArtifacts();
    }

    @Override
    public byte[] getImplementationArtifactContent(UUID id) {
        Bucket bucket = storage.get(implementationArtifactsBucketName);
        ImplementationArtifact implementationArtifact = ServiceUtils.findById(id, ImplementationArtifact.class, implementationArtifactRepository);
        Blob blob = bucket.get(implementationArtifact.getFileURL());
        return blob.getContent();
    }

    @Override
    public ImplementationArtifact update(UUID id, MultipartFile file) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }

    private ImplementationArtifact getImplementationArtifactFromBlob(Blob blob) {
        ImplementationArtifact implementationArtifact = new ImplementationArtifact();
        implementationArtifact.setName(blob.getName());
        implementationArtifact.setMimeType(blob.getContentType());
        implementationArtifact.setFileURL(blob.getName());
        implementationArtifact.setCreationDate(new Date(blob.getCreateTime()));
        implementationArtifact.setLastModifiedAt(new Date(blob.getUpdateTime()));
        return implementationArtifact;
    }
}
