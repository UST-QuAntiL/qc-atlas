package org.planqk.atlas.core.repository;

import org.planqk.atlas.core.model.CloudService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(exported = false)
public interface CloudServiceRepository extends JpaRepository<CloudService, UUID> {
    boolean existsCloudServiceById (UUID id);

    @Query("SELECT COUNT(cs) FROM CloudService cs JOIN cs.providedComputeResources backend WHERE backend.id = :backendId")
    long countCloudServiceByBackend(@Param("backendId") UUID backendId);
}
