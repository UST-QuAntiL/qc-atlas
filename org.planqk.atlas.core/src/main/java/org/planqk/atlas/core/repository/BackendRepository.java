package org.planqk.atlas.core.repository;

import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.Backend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BackendRepository extends JpaRepository<Backend, UUID> {

    Set<Backend> findByName(String name);
}
