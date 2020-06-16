package org.planqk.atlas.core.services;

import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.Backend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BackendService {

    Backend saveOrUpdate(Backend backend);

    Set<Backend> saveOrUpdateAll(Set<Backend> backends);

    Backend findById(UUID id);

    Set<Backend> findByName(String name);

    Page<Backend> findAll(Pageable pageable);

    void delete (UUID id);
}
