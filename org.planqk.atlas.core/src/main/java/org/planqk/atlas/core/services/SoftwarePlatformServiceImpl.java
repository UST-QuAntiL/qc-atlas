package org.planqk.atlas.core.services;

import lombok.AllArgsConstructor;
import org.planqk.atlas.core.model.Backend;
import org.planqk.atlas.core.model.CloudService;
import org.planqk.atlas.core.model.SoftwarePlatform;
import org.planqk.atlas.core.repository.SoftwarePlatformRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class SoftwarePlatformServiceImpl implements SoftwarePlatformService {

    private SoftwarePlatformRepository softwarePlatformRepository;

    @Override
    public SoftwarePlatform save(SoftwarePlatform softwarePlatform) {
        Set<Backend> supportedBackends = softwarePlatform.getSupportedBackends();
        for (Backend supportedBackend : supportedBackends) { }
        softwarePlatform.setSupportedBackends(supportedBackends);

        Set<CloudService> supportedCloudServices = softwarePlatform.getSupportedCloudServices();
        for (CloudService supportedCloudService : supportedCloudServices) { }
        softwarePlatform.setSupportedCloudServices(supportedCloudServices);

        return this.softwarePlatformRepository.save(softwarePlatform);
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
