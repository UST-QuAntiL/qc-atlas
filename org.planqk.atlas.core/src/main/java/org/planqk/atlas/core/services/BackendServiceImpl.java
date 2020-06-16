package org.planqk.atlas.core.services;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import lombok.AllArgsConstructor;

import org.planqk.atlas.core.model.Backend;
import org.planqk.atlas.core.model.exceptions.ConsistencyException;
import org.planqk.atlas.core.repository.BackendPropertyRepository;
import org.planqk.atlas.core.repository.BackendPropertyTypeRepository;
import org.planqk.atlas.core.repository.BackendRepository;
import org.planqk.atlas.core.repository.CloudServiceRepository;
import org.planqk.atlas.core.repository.SoftwarePlatformRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BackendServiceImpl implements BackendService {

    private final static Logger LOG = LoggerFactory.getLogger(BackendServiceImpl.class);

    private final BackendRepository repo;
    private final BackendPropertyRepository backendPropertyRepository;
    private final BackendPropertyTypeRepository backendPropertyTypeRepository;
    private final CloudServiceRepository cloudServiceRepository;
    private final SoftwarePlatformRepository softwarePlatformRepository;

    @Override
    public Backend saveOrUpdate(Backend backend) {
        if (backend.getId() != null) {
            return update(backend.getId(), backend);
        } else {
            backend.getBackendProperties().forEach(backendProperty -> {
                backendPropertyTypeRepository.save(backendProperty.getType());
                backendPropertyRepository.save(backendProperty);
            });
            return repo.save(backend);
        }
    }

    public Set<Backend> saveOrUpdateAll(Set<Backend> backends) {
        for (Backend backend : backends) {
            saveOrUpdate(backend);
        }
        return backends;
    }

    @Override
    public Backend findById(UUID id) {
        return repo.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Set<Backend> findByName(String name) {
        return repo.findByName(name);
    }

    @Override
    public Page<Backend> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    private Backend update(UUID id, Backend backend) {
        Backend persistedBackend = findById(id);

        persistedBackend.setQuantumComputationModel(backend.getQuantumComputationModel());
        persistedBackend.setTechnology(backend.getTechnology());
        persistedBackend.setName(backend.getName());
        persistedBackend.setVendor(backend.getVendor());
        persistedBackend.setBackendProperties(backend.getBackendProperties());
        persistedBackend.getBackendProperties().forEach(backendProperty -> {
            backendPropertyTypeRepository.save(backendProperty.getType());
            backendPropertyRepository.save(backendProperty);
        });

        return repo.save(persistedBackend);
    }

    @Override
    public void delete(UUID id) {
        // only delete if unused in SoftwarePlatforms and CloudServices
        long count = cloudServiceRepository.countCloudServiceByBackend(id) + softwarePlatformRepository.countSoftwarePlatformByBackend(id);
        if (count == 0) {
            
            repo.deleteById(id);
        } else {
            LOG.info("Trying to delete Backend that is used in a CloudService or SoftwarePlatform");
            throw new ConsistencyException(
                    "Cannot delete Backend since it is used by existing CloudService or SoftwarePlatform");
        }
    }
}
