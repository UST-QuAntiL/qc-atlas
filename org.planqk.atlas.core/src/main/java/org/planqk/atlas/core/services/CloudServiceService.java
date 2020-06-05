package org.planqk.atlas.core.services;

import org.planqk.atlas.core.model.CloudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;
import java.util.UUID;

public interface CloudServiceService {

    CloudService save(CloudService cloudService);

    Set<CloudService> createOrUpdateAll(Set<CloudService> cloudServices);

    CloudService createOrUpdate(CloudService cloudService);

    Page<CloudService> findAll(Pageable pageable);

    CloudService findById(UUID cloudServiceId);

    void delete(UUID cloudServiceId);

}
