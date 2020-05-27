package org.planqk.atlas.core.services;

import org.planqk.atlas.core.model.SoftwarePlatform;
import org.planqk.atlas.core.repository.SoftwarePlatformRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public class SoftwarePlatformServiceImpl implements SoftwarePlatformService {

    private SoftwarePlatformRepository softwarePlatformRepository;

    @Override
    public SoftwarePlatform save(SoftwarePlatform platform) {
        return this.softwarePlatformRepository.save(platform);
    }

    @Override
    public Page<SoftwarePlatform> findAll(Pageable pageable) {
        return softwarePlatformRepository.findAll(pageable);
    }

    @Override
    public Optional<SoftwarePlatform> findById(UUID platformId) {
        return softwarePlatformRepository.findById(platformId);
    }
}
