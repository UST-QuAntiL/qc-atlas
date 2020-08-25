package org.planqk.atlas.core.repository;

import java.util.UUID;

import org.planqk.atlas.core.model.SoftwarePlatform;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource(exported = false)
public interface SoftwarePlatformRepository extends JpaRepository<SoftwarePlatform, UUID> {

    Page<SoftwarePlatform> findAllByNameContainingIgnoreCase(String name, Pageable p);

    boolean existsSoftwarePlatformById (UUID id);

    @Query("SELECT sp " +
            "FROM SoftwarePlatform sp " +
            "JOIN sp.supportedCloudServices cs " +
            "WHERE cs.id = :csId")
    Page<SoftwarePlatform> findSoftwarePlatformsByCloudServiceId(@Param("csId") UUID cloudServiceId, Pageable pageable);

    @Query("SELECT sp " +
            "FROM SoftwarePlatform sp " +
            "JOIN sp.supportedComputeResources cr " +
            "WHERE cr.id = :crId")
    Page<SoftwarePlatform> findSoftwarePlatformsByComputeResourceId(@Param("crId") UUID computeResourceId, Pageable pageable);

    @Query("SELECT sp " +
            "FROM SoftwarePlatform sp " +
            "JOIN sp.implementations i " +
            "WHERE i.id = :implId")
    Page<SoftwarePlatform> findSoftwarePlatformsByImplementationId(@Param("implId") UUID implementationId, Pageable pageable);

    @Query("SELECT COUNT(sp) " +
            "FROM SoftwarePlatform sp " +
            "JOIN sp.supportedComputeResources cr " +
            "WHERE cr.id = :crId")
    long countSoftwarePlatformByComputeResource(@Param("crId") UUID computeResourceId);
}
