package org.planqk.atlas.core.repository;

import org.planqk.atlas.core.model.CloudService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;
import java.util.UUID;

@RepositoryRestResource(exported = false)
public interface CloudServiceRepository extends JpaRepository<CloudService, UUID> {
    Optional<CloudService> findByName(String name);
}
