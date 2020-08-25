package org.planqk.atlas.core.repository;

import java.util.UUID;

import org.planqk.atlas.core.model.ComputeResource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource(exported = false)
public interface ComputeResourceRepository extends JpaRepository<ComputeResource, UUID> {

    Page<ComputeResource> findAllByNameContainingIgnoreCase(String name, Pageable p);

    @Query("SELECT cr " +
            "FROM ComputeResource cr " +
            "JOIN cr.cloudServices cs " +
            "WHERE cs.id = :csId")
    Page<ComputeResource> findComputeResourcesByCloudServiceId(@Param("csId") UUID cloudServiceId, Pageable pageable);

    @Query("SELECT cr " +
            "FROM ComputeResource cr " +
            "JOIN cr.softwarePlatforms sp " +
            "WHERE sp.id = :spId")
    Page<ComputeResource> findComputeResourcesBySoftwarePlatformId(@Param("spId") UUID softwarePlatformId, Pageable pageable);
}
