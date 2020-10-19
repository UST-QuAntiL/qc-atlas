package org.planqk.atlas.core.repository;

import java.util.Optional;
import java.util.UUID;

import org.planqk.atlas.core.model.ImplementationArtifact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImplementationArtifactRepository extends JpaRepository<ImplementationArtifact, UUID> {

    Optional<ImplementationArtifact> findByFileURL(String fileURL);

    Page<ImplementationArtifact> findImplementationArtifactsByImplementation_Id(UUID implementationId, Pageable pageable);
}
