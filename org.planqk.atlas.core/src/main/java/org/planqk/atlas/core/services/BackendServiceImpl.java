package org.planqk.atlas.core.services;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import lombok.AllArgsConstructor;
import org.planqk.atlas.core.model.Backend;
import org.planqk.atlas.core.repository.BackendRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BackendServiceImpl implements BackendService {

    private BackendRepository repo;

    @Override
    public Backend save(Backend backend) {
        return repo.save(backend);
    }

    @Override
    public Optional<Backend> findOptionalById(UUID id) {
        return repo.findById(id);
    }

    @Override
    public Set<Backend> findByName(String name) {
        return repo.findByName(name);
    }

    @Override
    public Page<Backend> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }
}
