package org.planqk.atlas.core.services;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import org.planqk.atlas.core.exceptions.CloudStorageException;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.ImplementationArtifact;
import org.planqk.atlas.core.repository.ImplementationArtifactRepository;
import org.planqk.atlas.core.repository.ImplementationRepository;
import org.planqk.atlas.core.util.ServiceUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;

import lombok.RequiredArgsConstructor;

@Service
@Profile("stoneOne")
@RequiredArgsConstructor
public class ImplementationArtifactServiceCloudStorageImpl implements ImplementationArtifactService {

    private final Storage storage;

    @Value("${cloud.storage.implementation-artifacts-bucket-name}")
    private String implementationArtifactsBucketName;

    private final ImplementationArtifactRepository implementationArtifactRepository;

    private final ImplementationRepository implementationRepository;

    @Override
    public ImplementationArtifact create(UUID implementationId, MultipartFile file) {
        try {
            final BlobId blobId = BlobId.of(implementationArtifactsBucketName, implementationId + "/" + file.getOriginalFilename());
            final BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
            final Blob blob = storage.create(blobInfo, file.getBytes());
            final ImplementationArtifact implementationArtifact = getImplementationArtifactFromBlob(blob);

            implementationArtifactRepository.findByFileURL(implementationArtifact.getFileURL())
                .ifPresent(persistedImplementationArtifact -> implementationArtifact.setId(persistedImplementationArtifact.getId()));

            final Implementation implementation = ServiceUtils.findById(implementationId, Implementation.class, implementationRepository);
            implementationArtifact.setImplementation(implementation);
            return implementationArtifactRepository.save(implementationArtifact);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot read contents of multipart file");
        } catch (StorageException e) {
            throw new CloudStorageException("could not create in storage");
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
        Bucket bucket = this.storage.get(implementationArtifactsBucketName);
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
        ImplementationArtifact storedEntity = this.findById(id);
        BlobId blobId = BlobId.of(implementationArtifactsBucketName, storedEntity.getFileURL());
        try {
            storage.delete(blobId);
            this.implementationArtifactRepository.delete(storedEntity);
        } catch (StorageException e) {
            throw new CloudStorageException("could not delete from storage");
        }
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
