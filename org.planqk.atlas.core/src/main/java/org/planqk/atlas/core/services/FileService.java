package org.planqk.atlas.core.services;

import java.util.UUID;

import org.planqk.atlas.core.model.File;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    File create(MultipartFile file);

    File findById(UUID id);

    File update(UUID id, MultipartFile file);

    void delete(UUID id);

    byte[] getFileContent(UUID id);

}
