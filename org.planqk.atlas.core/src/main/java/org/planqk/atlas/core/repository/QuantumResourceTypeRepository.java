package org.planqk.atlas.core.repository;

import java.util.UUID;
import org.planqk.atlas.core.model.QuantumResourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface QuantumResourceTypeRepository extends JpaRepository<QuantumResourceType, UUID> {
}
