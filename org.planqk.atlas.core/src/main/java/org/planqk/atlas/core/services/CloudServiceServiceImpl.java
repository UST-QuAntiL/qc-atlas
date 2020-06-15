package org.planqk.atlas.core.services;

import lombok.AllArgsConstructor;

import org.planqk.atlas.core.model.CloudService;
import org.planqk.atlas.core.repository.CloudServiceRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class CloudServiceServiceImpl implements CloudServiceService {

    private final CloudServiceRepository cloudServiceRepository;
    private final BackendService backendService;

    @Override
    public CloudService save(CloudService cloudService) {
        cloudService.setProvidedBackends(backendService.saveOrUpdateAll(cloudService.getProvidedBackends()));
        return this.cloudServiceRepository.save(cloudService);
    }

    @Override
    public Set<CloudService> createOrUpdateAll(Set<CloudService> cloudServices) {
        return cloudServices.stream().map(this::save).collect(Collectors.toSet());
    }

    @Transactional
    @Override
    public CloudService update(UUID id, CloudService cloudService) {
        if (cloudServiceRepository.existsCloudServiceById(id)) {
            return save(cloudService);
        }

        throw new NoSuchElementException();
    }

    @Override
    public Page<CloudService> findAll(Pageable pageable) {
        return cloudServiceRepository.findAll(pageable);
    }

    @Override
    public CloudService findById(UUID cloudServiceId) {
        return cloudServiceRepository.findById(cloudServiceId).orElseThrow(NoSuchElementException::new);
    }

    @Transactional
    @Override
    public void delete(UUID cloudServiceId) {
        cloudServiceRepository.deleteById(cloudServiceId);
    }
}
