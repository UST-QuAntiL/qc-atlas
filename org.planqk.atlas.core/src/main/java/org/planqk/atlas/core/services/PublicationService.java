package org.planqk.atlas.core.services;


import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.Publication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;


public interface PublicationService {
    Publication save(Publication publication);

    Publication update(UUID pubId, Publication publication);

    void deleteById(UUID pubId);

    Page<Publication> findAll(Pageable pageable);

    Publication findById(UUID pubId);

    Optional<Publication> findOptionalById(UUID pubId);

	Set<Publication> createOrUpdateAll(Set<Publication> publications);

	Set<Algorithm> findPublicationAlgorithms(UUID publicationId);

    void deletePublicationsByIds(Set<UUID> publicationIds);

}
