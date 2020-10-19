package org.planqk.atlas.core.services;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
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
//        if(blob.exists(Blob.BlobSourceOption.generationMatch(implementationId + "/" + file.getOriginalFilename()))){
//            System.out.println("Exists");
//        } else {
//            System.out.println("Not Exists");
//        }
        try {
            blob = bucket.create(implementationId + "/" + file.getOriginalFilename(), file.getBytes(), file.getContentType());
        } catch (IOException e) {
            e.printStackTrace();
        }

        ImplementationArtifact implementationArtifact = getImplementationArtifactFromBlob(blob);
        Implementation implementation = ServiceUtils.findById(implementationId, Implementation.class, implementationRepository);
        implementationArtifact.setImplementation(implementation);
        return implementationArtifactRepository.save(implementationArtifact);
    }

    @Override
    public ImplementationArtifact findByImplementationIdAndName(UUID implementationId, String artifactName) {
        Bucket bucket = storage.get(implementationArtifactsBucketName);
        Blob blob = bucket.get(implementationId.toString() + "/" + artifactName);
        ImplementationArtifact implementationArtifact = getImplementationArtifactFromBlob(blob);
        return implementationArtifact;
    }

    @Override
    public ImplementationArtifact findById(UUID id) {
        return ServiceUtils.findById(id, ImplementationArtifact.class, implementationArtifactRepository);
    }

    @Override
    public Collection<ImplementationArtifact> findAllByImplementationId(UUID implementationId) {
        Bucket bucket = storage.get(implementationArtifactsBucketName);
        Collection<ImplementationArtifact> implementationArtifacts = new HashSet<ImplementationArtifact>();
        Page<Blob> blobs = bucket.list(Storage.BlobListOption.prefix(implementationId.toString()));
        for (Blob blob : blobs.getValues()) {
            implementationArtifacts.add(getImplementationArtifactFromBlob(blob));
        }
        return implementationArtifacts;
    }

    @Override
    public byte[] getImplementationArtifactContent(UUID implementationId, String artifactName) {
        Bucket bucket = storage.get(implementationArtifactsBucketName);
        Blob blob = bucket.get(implementationId.toString() + "/" + artifactName);
        return blob.getContent();
    }

    @Override
    public ImplementationArtifact update(MultipartFile file) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }

    private ImplementationArtifact getImplementationArtifactFromBlob(Blob blob) {
        ImplementationArtifact implementationArtifact = new ImplementationArtifact();
        implementationArtifact.setName(blob.getName());
        implementationArtifact.setMimeType(blob.getContentType());
        implementationArtifact.setFileURL(blob.getMediaLink());
        implementationArtifact.setCreationDate(new Date(blob.getCreateTime()));
        implementationArtifact.setLastModifiedAt(new Date(blob.getUpdateTime()));
        return implementationArtifact;
    }
}
