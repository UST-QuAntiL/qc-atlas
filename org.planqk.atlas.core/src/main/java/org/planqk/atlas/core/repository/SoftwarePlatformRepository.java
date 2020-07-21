package org.planqk.atlas.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import org.planqk.atlas.core.model.SoftwarePlatform;

import java.util.UUID;

@RepositoryRestResource(exported = false)
public interface SoftwarePlatformRepository extends JpaRepository<SoftwarePlatform, UUID> {
    boolean existsSoftwarePlatformById (UUID id);

    @Query("SELECT COUNT(sp) " +
            "FROM SoftwarePlatform sp " +
            "JOIN sp.supportedComputeResources computeResource " +
            "WHERE computeResource.id = :computeResourceId")
    long countSoftwarePlatformByBackend(@Param("computeResourceId") UUID computeResourceId);
}
