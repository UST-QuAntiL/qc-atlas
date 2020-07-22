package org.planqk.atlas.core.repository;

import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.ComputeResource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ComputeResourceRepository extends JpaRepository<ComputeResource, UUID> {
    @Query("SELECT cr FROM ComputeResource cr JOIN cr.cloudServices cs WHERE cs.id = :csid")
    Page<ComputeResource> findComputeResourcesByCloudServiceId(@Param("csid") UUID csid, Pageable p);

    @Query("SELECT cr FROM ComputeResource cr JOIN cr.softwarePlatforms sp WHERE sp.id = :spid")
    Page<ComputeResource> findComputeResourcesBySoftwarePlatformId(@Param("spid") UUID spid, Pageable p);

    Set<ComputeResource> findByName(String name);
}
