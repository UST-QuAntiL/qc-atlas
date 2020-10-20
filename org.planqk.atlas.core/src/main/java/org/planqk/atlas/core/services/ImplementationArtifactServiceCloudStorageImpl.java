package org.planqk.atlas.core.services;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.ImplementationArtifact;
import org.planqk.atlas.core.repository.ImplementationArtifactRepository;
import org.planqk.atlas.core.repository.ImplementationRepository;
import org.planqk.atlas.core.util.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;

import lombok.AllArgsConstructor;

@Service
@Profile("stoneOne")
@AllArgsConstructor
public class ImplementationArtifactServiceCloudStorageImpl implements ImplementationArtifactService {

    @Autowired
    private final Storage storage;

    private final String implementationArtifactsBucketName = "planqk-algo-artifacts";

    private final ImplementationArtifactRepository implementationArtifactRepository;

    private final ImplementationRepository implementationRepository;

    @Override
    public ImplementationArtifact create(UUID implementationId, MultipartFile file) {
        try {
            Bucket bucket = storage.get(implementationArtifactsBucketName);
            Blob blob = bucket.create(implementationId + "/" + file.getOriginalFilename(), file.getBytes(), file.getContentType());
            ImplementationArtifact implementationArtifact = getImplementationArtifactFromBlob(blob);
            Optional<ImplementationArtifact> persistedImplementationArtifactOptional =
                    implementationArtifactRepository.findByFileURL(implementationArtifact.getFileURL());
            if (persistedImplementationArtifactOptional.isPresent()) {
                implementationArtifact.setId(persistedImplementationArtifactOptional.get().getId());
            }
            Implementation implementation = ServiceUtils.findById(implementationId, Implementation.class, implementationRepository);
            implementationArtifact.setImplementation(implementation);
            return implementationArtifactRepository.save(implementationArtifact);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot read contents of multipart file");
        }
    }

    @Override
    public ImplementationArtifact findById(UUID id) {
        return ServiceUtils.findById(id, ImplementationArtifact.class, implementationArtifactRepository);
    }

    @Override
    public Page<ImplementationArtifact> findAllByImplementationId(UUID implementationId, Pageable pageable) {
        return implementationArtifactRepository.findImplementationArtifactsByImplementation_Id(implementationId, pageable);
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
