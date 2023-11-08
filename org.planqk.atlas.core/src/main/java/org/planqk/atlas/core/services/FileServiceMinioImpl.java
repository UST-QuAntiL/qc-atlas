/*******************************************************************************
 * Copyright (c) 2020-2023 the qc-atlas contributors.
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package org.planqk.atlas.core.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.UUID;
import javax.xml.bind.DatatypeConverter;

import org.planqk.atlas.core.exceptions.CloudStorageException;
import org.planqk.atlas.core.model.File;
import org.planqk.atlas.core.repository.FileRepository;
import org.planqk.atlas.core.util.ServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;


@Service
@Profile("!google-cloud & minio")
@RequiredArgsConstructor
public class FileServiceMinioImpl implements FileService {

    @Autowired
    private final MinioClient minioClient;

    private final FileRepository fileRepository;

    @Value("${cloud.storage.implementation-files-bucket-name}")
    private String implementationFilesBucketName;

    @SuppressWarnings("checkstyle:FinalLocalVariable")
    @Override
    public File create(MultipartFile file) {
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(file.getBytes());
            final String md5Hash = DatatypeConverter
                    .printHexBinary(md.digest()).toUpperCase();
            final ByteArrayInputStream bais = new ByteArrayInputStream(file.getBytes());
            final String fileID = md5Hash + "/" + file.getOriginalFilename();
            checkBucket();
            final ObjectWriteResponse objectWriteResponse = minioClient.putObject(PutObjectArgs
                    .builder()
                    .bucket(implementationFilesBucketName)
                    .object(fileID)
                    .stream(bais, bais.available(), -1)
                    .build());
            bais.close();
            final StatObjectResponse statObjectResponse = minioClient.statObject(
                    StatObjectArgs.builder().bucket(implementationFilesBucketName).object(fileID).build());
            final File implementationFile = new File();
            implementationFile.setName(file.getOriginalFilename());
            implementationFile.setMimeType(statObjectResponse.contentType());
            implementationFile.setFileURL(objectWriteResponse.object());
            implementationFile.setCreationDate(Date.from(statObjectResponse.lastModified().toInstant()));
            implementationFile.setLastModifiedAt(Date.from(statObjectResponse.lastModified().toInstant()));

            return fileRepository.save(implementationFile);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot read contents of multipart file");
        } catch (MinioException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new CloudStorageException("Could not create file in storage");
        }
    }

    @Override
    public File findById(UUID fileId) {
        return ServiceUtils.findById(fileId, File.class, fileRepository);
    }

    @Override
    public void delete(UUID id) {
        final File file = ServiceUtils.findById(id, File.class, fileRepository);
        try {
            checkBucket();
            minioClient.removeObject(
                    RemoveObjectArgs
                            .builder()
                            .bucket(implementationFilesBucketName)
                            .object(file.getFileURL())
                            .build());
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            throw new CloudStorageException("Could not get file from storage");
        }
    }

    @Override
    public byte[] getFileContent(UUID id) {
        final File file = ServiceUtils.findById(id, File.class, fileRepository);
        try {
            checkBucket();
            final InputStream stream =
                    minioClient.getObject(GetObjectArgs
                            .builder()
                            .bucket(implementationFilesBucketName)
                            .object(file.getFileURL())
                            .build());
            return stream.readAllBytes();
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new CloudStorageException("Could not get file from storage");
        }
    }

    private void checkBucket() throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        final boolean found =
                minioClient.bucketExists(BucketExistsArgs.builder().bucket(implementationFilesBucketName).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(implementationFilesBucketName).build());
        }
    }
}
