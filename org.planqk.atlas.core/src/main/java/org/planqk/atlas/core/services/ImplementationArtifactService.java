package org.planqk.atlas.core.services;

import java.util.Collection;
import java.util.UUID;

import org.planqk.atlas.core.model.ImplementationArtifact;
import org.springframework.web.multipart.MultipartFile;

public interface ImplementationArtifactService {

    ImplementationArtifact create(UUID implementationId, MultipartFile file);

    ImplementationArtifact findByImplementationIdAndName(UUID implementationId, String artifactName);

    Collection<ImplementationArtifact> findAllByImplementationId(UUID implementationId);

    ImplementationArtifact findById(UUID implementationId);

    ImplementationArtifact update(MultipartFile file);

    void delete(UUID id);

    byte[] getImplementationArtifactContent(UUID implementationId, String artifactName);

}
