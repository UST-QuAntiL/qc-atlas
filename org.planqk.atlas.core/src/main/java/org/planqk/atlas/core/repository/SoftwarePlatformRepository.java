package org.planqk.atlas.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import org.planqk.atlas.core.model.SoftwarePlatform;

import java.util.UUID;

@RepositoryRestResource(exported = false)
public interface SoftwarePlatformRepository extends JpaRepository<SoftwarePlatform, UUID> {
}
