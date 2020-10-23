package org.planqk.atlas.core.services;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import org.planqk.atlas.core.exceptions.CloudStorageException;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.File;
import org.planqk.atlas.core.repository.FileRepository;
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
public class FileServiceCloudStorageImpl implements FileService {

    private final Storage storage;

    @Value("${cloud.storage.implementation-files-bucket-name}")
    private String implementationFilesBucketName;

    private final FileRepository fileRepository;

    private final ImplementationRepository implementationRepository;

    @Override
    public File create(UUID implementationId, MultipartFile file) {
        try {
            final BlobId blobId = BlobId.of(implementationFilesBucketName, implementationId + "/" + file.getOriginalFilename());
            final BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
            final Blob blob = storage.create(blobInfo, file.getBytes());
            final File implementationFile = getFileFromBlob(blob);

            fileRepository.findByFileURL(implementationFile.getFileURL())
                .ifPresent(persistedFile -> implementationFile.setId(persistedFile.getId()));

            final Implementation implementation = ServiceUtils.findById(implementationId, Implementation.class, implementationRepository);
            implementationFile.setImplementation(implementation);
            return fileRepository.save(implementationFile);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot read contents of multipart file");
        } catch (StorageException e) {
            throw new CloudStorageException("could not create in storage");
        }
    }

    @Override
    public File findById(UUID id) {
        return ServiceUtils.findById(id, File.class, fileRepository);
    }

    @Override
    public Page<File> findAllByImplementationId(UUID implementationId, Pageable pageable) {
        return fileRepository.findFilesByImplementation_Id(implementationId, pageable);
    }

    @Override
    public byte[] getFileContent(UUID id) {
        Bucket bucket = this.storage.get(implementationFilesBucketName);
        File file = ServiceUtils.findById(id, File.class, fileRepository);
        Blob blob = bucket.get(file.getFileURL());
        return blob.getContent();
    }

    @Override
    public File update(UUID id, MultipartFile file) {
        return null;
    }

    @Override
    public void delete(UUID id) {
        File storedEntity = this.findById(id);
        BlobId blobId = BlobId.of(implementationFilesBucketName, storedEntity.getFileURL());
        try {
            storage.delete(blobId);
            this.fileRepository.delete(storedEntity);
        } catch (StorageException e) {
            throw new CloudStorageException("could not delete from storage");
        }
    }

    private File getFileFromBlob(Blob blob) {
        File file = new File();
        file.setName(blob.getName());
        file.setMimeType(blob.getContentType());
        file.setFileURL(blob.getName());
        file.setCreationDate(new Date(blob.getCreateTime()));
        file.setLastModifiedAt(new Date(blob.getUpdateTime()));
        return file;
    }
}
