package org.planqk.atlas.core.services;

import lombok.AllArgsConstructor;
import org.planqk.atlas.core.model.Backend;
import org.planqk.atlas.core.model.CloudService;
import org.planqk.atlas.core.model.SoftwarePlatform;
import org.planqk.atlas.core.repository.SoftwarePlatformRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class SoftwarePlatformServiceImpl implements SoftwarePlatformService {

    private final SoftwarePlatformRepository softwarePlatformRepository;
    private final CloudServiceService cloudServiceService;
    private final BackendService backendService;

    @Override
    public SoftwarePlatform save(SoftwarePlatform softwarePlatform) {
        backendService.saveOrUpdateAll(softwarePlatform.getSupportedBackends());
        softwarePlatform.setSupportedCloudServices(
                cloudServiceService.createOrUpdateAll(softwarePlatform.getSupportedCloudServices()));

        return this.softwarePlatformRepository.save(softwarePlatform);
    }


    @Override
    public Page<SoftwarePlatform> findAll(Pageable pageable) {
        return softwarePlatformRepository.findAll(pageable);
    }

    @Override
    public SoftwarePlatform findById(UUID platformId) {
        return softwarePlatformRepository.findById(platformId).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public SoftwarePlatform update(UUID id, SoftwarePlatform softwarePlatform) {
        SoftwarePlatform persistedSoftwarePlatform = softwarePlatformRepository.findById(id).orElseThrow(NoSuchElementException::new);

        persistedSoftwarePlatform.setLink(softwarePlatform.getLink());
        persistedSoftwarePlatform.setName(softwarePlatform.getName());
        persistedSoftwarePlatform.setVersion(softwarePlatform.getVersion());
        persistedSoftwarePlatform.setSupportedBackends(softwarePlatform.getSupportedBackends());

        return save(persistedSoftwarePlatform);
    }

    @Override
    public void delete(UUID platformId) {
        softwarePlatformRepository.deleteById(platformId);
    }

}
