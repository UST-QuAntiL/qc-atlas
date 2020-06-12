package org.planqk.atlas.core.services;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.Backend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BackendService {

    Backend save(Backend algoRelationType);

    Optional<Backend> findOptionalById(UUID id);

    Set<Backend> findByName(String name);

    Page<Backend> findAll(Pageable pageable);
}
