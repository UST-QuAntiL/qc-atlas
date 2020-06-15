package org.planqk.atlas.core.services;

import lombok.AllArgsConstructor;

import org.planqk.atlas.core.model.SoftwarePlatform;
import org.planqk.atlas.core.repository.SoftwarePlatformRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SoftwarePlatformServiceImpl implements SoftwarePlatformService {

    private final SoftwarePlatformRepository softwarePlatformRepository;
    private final CloudServiceService cloudServiceService;
    private final BackendService backendService;

    @Override
    public SoftwarePlatform save(SoftwarePlatform softwarePlatform) {
        backendService.saveOrUpdateAll(softwarePlatform.getSupportedBackends());
        cloudServiceService.createOrUpdateAll(softwarePlatform.getSupportedCloudServices());

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
        if (softwarePlatformRepository.existsSoftwarePlatformById(id)) {
            softwarePlatform.setId(id);
            return save(softwarePlatform);
        }
        throw new NoSuchElementException();
    }

    @Override
    public void delete(UUID platformId) {
        softwarePlatformRepository.deleteById(platformId);
    }
}
