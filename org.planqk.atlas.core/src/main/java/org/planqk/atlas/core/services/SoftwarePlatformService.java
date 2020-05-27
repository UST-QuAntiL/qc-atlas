package org.planqk.atlas.core.services;

import org.planqk.atlas.core.model.SoftwarePlatform;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface SoftwarePlatformService {

    SoftwarePlatform save(SoftwarePlatform tag);

    Page<SoftwarePlatform> findAll(Pageable pageable);

    Optional<SoftwarePlatform> findById(UUID platformId);
}
