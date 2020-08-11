package org.planqk.atlas.core.repository;

import java.util.UUID;

import org.planqk.atlas.core.model.SoftwarePlatform;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface SoftwarePlatformRepository extends JpaRepository<SoftwarePlatform, UUID> {
    Page<SoftwarePlatform> findAllByNameContainingIgnoreCase(String name, Pageable p);

    boolean existsSoftwarePlatformById (UUID id);

    @Query("SELECT sp FROM SoftwarePlatform sp JOIN sp.supportedCloudServices cs WHERE cs.id = :spid")
    Page<SoftwarePlatform> findSoftwarePlatformsByCloudServiceId(@Param("spid") UUID id, Pageable p);

    @Query("SELECT sp FROM SoftwarePlatform sp JOIN sp.supportedComputeResources cr WHERE cr.id = :spid")
    Page<SoftwarePlatform> findSoftwarePlatformsByComputeResourceId(@Param("spid") UUID id, Pageable p);

    @Query("SELECT sp FROM SoftwarePlatform sp JOIN sp.implementations i WHERE i.id = :spid")
    Page<SoftwarePlatform> findSoftwarePlatformsByImplementationId(@Param("spid") UUID id, Pageable p);

    @Query("SELECT COUNT(sp) " +
            "FROM SoftwarePlatform sp " +
            "JOIN sp.supportedComputeResources computeResource " +
            "WHERE computeResource.id = :computeResourceId")
    long countSoftwarePlatformByComputeResource(@Param("computeResourceId") UUID computeResourceId);
}
