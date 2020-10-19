package org.planqk.atlas.core.services;

import java.util.UUID;

import org.planqk.atlas.core.model.ImplementationArtifact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ImplementationArtifactService {

    ImplementationArtifact create(UUID implementationId, MultipartFile file);

    Page<ImplementationArtifact> findAllByImplementationId(UUID implementationId, Pageable pageable);

    ImplementationArtifact findById(UUID implementationId);

    ImplementationArtifact update(UUID artifactId, MultipartFile file);

    void delete(UUID id);

    byte[] getImplementationArtifactContent(UUID id);

}
