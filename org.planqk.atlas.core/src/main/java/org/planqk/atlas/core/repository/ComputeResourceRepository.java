package org.planqk.atlas.core.repository;

import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.ComputeResource;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ComputeResourceRepository extends JpaRepository<ComputeResource, UUID> {
    Set<ComputeResource> findByName(String name);
}
