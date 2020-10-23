package org.planqk.atlas.core.services;

import java.util.UUID;

import org.planqk.atlas.core.model.File;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;

@Service
@Profile("!google-cloud")
@AllArgsConstructor
public class FileServiceImpl implements FileService {


    @Override
    public File create(UUID implementationId, MultipartFile file) {
        return null;
    }

    @Override
    public Page<File> findAllByImplementationId(UUID implementationId, Pageable pageable) {
        return null;
    }

    @Override
    public File findById(UUID implementationId) {
        return null;
    }

    @Override
    public File update(UUID artifactId, MultipartFile file) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }

    @Override
    public byte[] getFileContent(UUID id) {
        return new byte[0];
    }
}
