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

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.UUID;

import org.planqk.atlas.core.model.File;
import org.planqk.atlas.core.model.FileData;
import org.planqk.atlas.core.repository.FileDataRepository;
import org.planqk.atlas.core.repository.FileRepository;
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

    private final FileDataRepository fileDataRepository;

    @Override
    public File create(MultipartFile file) {
        final InputStream inputStream;
        try {
            inputStream = file.getInputStream();

            final File createdFile = new File();
            createdFile.setName(file.getOriginalFilename());

            var contentType = file.getContentType();

            if (contentType == null) {
                contentType = URLConnection.guessContentTypeFromName(file.getOriginalFilename());
            }

            createdFile.setMimeType(contentType);
            createdFile.setFileURL(file.getOriginalFilename());

            final FileData fileData = new FileData();
            fileData.setData(inputStream.readAllBytes());
            fileData.setFile(createdFile);

            final File savedFile = fileRepository.save(createdFile);
            fileDataRepository.save(fileData);
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
        final File file = findById(id);
        final var fileData = fileDataRepository.findByFile(file);
        fileDataRepository.delete(fileData);
        fileRepository.delete(file);
    }

    @Override
    public byte[] getFileContent(UUID id) {
        final File file = findById(id);
        final FileData fileData = fileDataRepository.findByFile(file);
        return fileData.getData();
    }
}
