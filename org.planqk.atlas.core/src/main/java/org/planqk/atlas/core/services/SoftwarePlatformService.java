package org.planqk.atlas.core.services;

import org.planqk.atlas.core.model.SoftwarePlatform;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

import javax.transaction.Transactional;

public interface SoftwarePlatformService {

    @Transactional
    SoftwarePlatform save(SoftwarePlatform softwarePlatform);

    Page<SoftwarePlatform> findAll(Pageable pageable);

    SoftwarePlatform findById(UUID platformId);

    SoftwarePlatform update(UUID id, SoftwarePlatform softwarePlatform);

    void delete(UUID platformId);
}
