package org.planqk.atlas.core.services;

import java.util.Collection;
import java.util.UUID;

import org.planqk.atlas.core.model.ImplementationArtifact;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;

@Service
@Profile("!stoneOne")
@AllArgsConstructor
public class ImplementationArtifactServiceImpl implements ImplementationArtifactService {


    @Override
    public ImplementationArtifact create(UUID implementationId,
                                         MultipartFile file) {
        return null;
    }

    @Override
    public ImplementationArtifact findByImplementationIdAndName(UUID implementationId, String artifactName) {
        return null;
    }

    @Override
    public Collection<ImplementationArtifact> findAllByImplementationId(UUID implementationId) {
        return null;
    }

    @Override
    public ImplementationArtifact findById(UUID implementationId) {
        return null;
    }

    @Override
    public ImplementationArtifact update(MultipartFile file) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }

    @Override
    public byte[] getImplementationArtifactContent(UUID implementationId, String artifactName) {
        return new byte[0];
    }
}
