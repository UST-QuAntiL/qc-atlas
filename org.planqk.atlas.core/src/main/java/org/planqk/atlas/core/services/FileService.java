package org.planqk.atlas.core.services;

import java.util.UUID;

import org.planqk.atlas.core.model.File;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    File create(UUID implementationId, MultipartFile file);

    Page<File> findAllByImplementationId(UUID implementationId, Pageable pageable);

    File findById(UUID implementationId);

    File update(UUID artifactId, MultipartFile file);

    void delete(UUID id);

    byte[] getFileContent(UUID id);

}
