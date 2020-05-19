package org.planqk.atlas.core.services;

import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.planqk.atlas.core.model.QuantumResourceType;
import org.planqk.atlas.core.repository.QuantumResourceTypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class QuantumResourceTypeServiceImpl implements QuantumResourceTypeService {

    private QuantumResourceTypeRepository repo;

    @Override
    public QuantumResourceType save(QuantumResourceType qpu) {
        return null;
    }

    @Override
    public Page<QuantumResourceType> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<QuantumResourceType> findById(UUID qpuId) {
        return Optional.empty();
    }
}
