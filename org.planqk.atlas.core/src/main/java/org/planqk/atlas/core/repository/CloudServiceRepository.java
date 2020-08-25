package org.planqk.atlas.core.repository;

import java.util.UUID;

import org.planqk.atlas.core.model.CloudService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource(exported = false)
public interface CloudServiceRepository extends JpaRepository<CloudService, UUID> {
    Page<CloudService> findAllByNameContainingIgnoreCase(String name, Pageable p);

    boolean existsCloudServiceById(UUID id);

    @Query("SELECT cs " +
            "FROM CloudService cs " +
            "JOIN cs.softwarePlatforms sp " +
            "WHERE sp.id = :spId")
    Page<CloudService> findCloudServicesBySoftwarePlatformId(@Param("spId") UUID softwarePlatformId, Pageable pageable);

    @Query("SELECT cs " +
            "FROM CloudService cs " +
            "JOIN cs.providedComputeResources cr " +
            "WHERE cr.id = :crId")
    Page<CloudService> findCloudServicesByComputeResourceId(@Param("crId") UUID computeResourceId, Pageable pageable);

    @Query("SELECT COUNT(cs) " +
            "FROM CloudService cs " +
            "JOIN cs.providedComputeResources cr " +
            "WHERE cr.id = :crId")
    long countCloudServiceByComputeResource(@Param("crId") UUID computeResourceId);
}
