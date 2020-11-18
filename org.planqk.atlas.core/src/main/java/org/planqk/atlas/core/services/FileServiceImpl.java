/*******************************************************************************
 * Copyright (c) 2020 the qc-atlas contributors.
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.planqk.atlas.core.model.File;
import org.planqk.atlas.core.repository.FileRepository;
import org.planqk.atlas.core.repository.ImplementationPackageRepository;
import org.planqk.atlas.core.util.ServiceUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile("!google-cloud")
@AllArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;

    private final ImplementationPackageRepository implementationPackageRepository;

    private final String path = System.getProperty("java.io.tmpdir");

    private final String storageFolder = path + java.io.File.separator + "qc-atlas";

    @Override
    public File create(UUID implementationPackageId, MultipartFile file) {
        final InputStream inputStream;
        final OutputStream outputStream;

        final java.io.File dir = new java.io.File(storageFolder);
        if (!dir.exists())
            dir.mkdirs();
        final String fileName = String.valueOf(implementationPackageId);
        final java.io.File newFile = new java.io.File(dir.getAbsolutePath() + java.io.File.separator + implementationPackageId + "_" +
                file.getOriginalFilename());

        try {
            inputStream = file.getInputStream();

            if (!newFile.exists()) {
                newFile.createNewFile();
            }
            outputStream = new FileOutputStream(newFile);
            int read = 0;
            final int maxSize = 1024;
            final byte[] bytes = new byte[maxSize];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            outputStream.close();

            final File createdFile = new File();
            createdFile.setName(file.getOriginalFilename());
            createdFile.setMimeType(file.getContentType());
            createdFile.setFileURL(newFile.getAbsolutePath());

            final File savedFile = fileRepository.save(createdFile);
            return savedFile;


        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public File findById(UUID fileId) {
        return ServiceUtils.findById(fileId, File.class, fileRepository);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        final String url = findById(id).getFileURL();
        try {
            Files.deleteIfExists(Paths.get(url));
            fileRepository.deleteById(id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] getFileContent(UUID id) {
        final File file = findById(id);
        try {
            return Files.readAllBytes(Paths.get(file.getFileURL()));
        } catch (IOException e) {
            throw new NoSuchElementException("File with URL \"" + file.getFileURL() + "\" does not exist");
        }
    }
}
