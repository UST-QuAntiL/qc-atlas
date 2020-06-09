package org.planqk.atlas.core.services;

import org.planqk.atlas.core.model.SoftwarePlatform;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface SoftwarePlatformService {

    @Transactional
    SoftwarePlatform save(SoftwarePlatform softwarePlatform);

    Page<SoftwarePlatform> findAll(Pageable pageable);

    SoftwarePlatform findById(UUID platformId);

    @Transactional
    void delete(UUID platformId);
}
