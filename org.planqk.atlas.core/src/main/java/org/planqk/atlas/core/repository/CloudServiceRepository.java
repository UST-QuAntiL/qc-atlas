package org.planqk.atlas.core.repository;

import java.util.UUID;

import org.planqk.atlas.core.model.CloudService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface CloudServiceRepository extends JpaRepository<CloudService, UUID> {
    boolean existsCloudServiceById(UUID id);

    @Query("SELECT cs FROM CloudService cs JOIN cs.softwarePlatforms sp WHERE sp.id = :spid")
    Page<CloudService> findCloudServicesBySoftwarePlatformId(@Param("spid") UUID id, Pageable p);

    @Query("SELECT cs FROM CloudService cs JOIN cs.providedComputeResources cr WHERE cr.id = :spid")
    Page<CloudService> findCloudServicesByComputeResourceId(@Param("spid") UUID id, Pageable p);

    @Query("SELECT COUNT(cs) " +
            "FROM CloudService cs " +
            "JOIN cs.providedComputeResources computeResource " +
            "WHERE computeResource.id = :computeResourceId")
    long countCloudServiceByComputeResource(@Param("computeResourceId") UUID computeResourceId);
}
