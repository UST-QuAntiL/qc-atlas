package org.planqk.atlas.core.services;

import org.planqk.atlas.core.model.CloudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

public interface CloudServiceService {

    @Transactional
    CloudService save(CloudService cloudService);

    @Transactional
    Set<CloudService> createOrUpdateAll(Set<CloudService> cloudServices);

    @Transactional
    CloudService createOrUpdate(CloudService cloudService);

    Page<CloudService> findAll(Pageable pageable);

    CloudService findById(UUID cloudServiceId);

    @Transactional
    void delete(UUID cloudServiceId);

}
