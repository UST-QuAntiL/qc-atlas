package org.planqk.atlas.core.services;


import org.planqk.atlas.core.model.Publication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;


public interface PublicationService {
    Publication save(Publication publication);

    Publication update(UUID pubId, Publication publication);

    void deleteById(UUID pubId);

    Page<Publication> findAll(Pageable pageable);

    Publication findById(UUID pubId);

    Optional<Publication> findOptionalById(UUID pubId);
}
