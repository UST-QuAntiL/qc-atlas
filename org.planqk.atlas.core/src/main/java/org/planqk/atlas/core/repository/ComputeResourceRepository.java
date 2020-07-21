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
    @Query("SELECT cs FROM CloudService cs JOIN cs.providedComputeResources res WHERE res.id = :csid")
    Page<ComputeResource> findComputeResourceByCloudServiceId(@Param("csid") UUID csid, Pageable p);

    @Query("SELECT sp FROM SoftwarePlatform sp JOIN sp.supportedComputeResources res WHERE res.id = :spid")
    Page<ComputeResource> findComputeResourceBySoftwarePlatformId(@Param("spid") UUID spid, Pageable p);

    Set<ComputeResource> findByName(String name);
}
