package org.planqk.atlas.core.services;

import java.util.Optional;
import java.util.UUID;
import org.planqk.atlas.core.model.Qpu;
import org.planqk.atlas.core.model.QuantumResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuantumResourceTypeService {
    QuantumResourceType save(QuantumResourceType resourceType);

    Page<QuantumResourceType> findAll(Pageable pageable);

    Optional<QuantumResourceType> findById(UUID resourceTypeId);
}
