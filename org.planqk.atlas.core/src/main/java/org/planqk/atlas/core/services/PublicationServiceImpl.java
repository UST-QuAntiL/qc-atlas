package org.planqk.atlas.core.services;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.repository.AlgorithmRepository;
import org.planqk.atlas.core.repository.PublicationRepository;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class PublicationServiceImpl implements PublicationService {

    private final PublicationRepository publicationRepository;
    private final AlgorithmRepository algorithmRepository;

    @Override
    @Transactional
    public Publication save(Publication publication) {
        return publicationRepository.save(publication);
    }

    @Override
    @Transactional
    public Publication update(UUID id, Publication publication) {
        var existingPublication = publicationRepository.findById(id).orElseThrow(NoSuchElementException::new);

        fillExistingPublication(publication, existingPublication);
        return publicationRepository.save(existingPublication);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        publicationRepository.deleteById(id);
    }

    @Override
    public Page<Publication> findAll(Pageable pageable) {
        return publicationRepository.findAll(pageable);
    }

    @Override
    public Publication findById(UUID pubId) {

        return findOptionalById(pubId).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Optional<Publication> findOptionalById(UUID pubId) {
        return publicationRepository.findById(pubId);
    }

    @Override
    @Transactional
    public Set<Publication> createOrUpdateAll(Set<Publication> publications) {
        if (publications == null) {
            return Set.of();
        }

        return new HashSet<>(this.publicationRepository.saveAll(publications));
    }

    private void fillExistingPublication(Publication updatedPublication, Publication persistedPublication) {
        persistedPublication.setTitle(updatedPublication.getTitle());
        persistedPublication.setDoi(updatedPublication.getDoi());
        persistedPublication.setUrl(updatedPublication.getUrl());
        persistedPublication.setAuthors(updatedPublication.getAuthors());
    }

    @Override
    public Set<Algorithm> findPublicationAlgorithms(UUID publicationId) {
        return algorithmRepository.getAlgorithmsWithPublicationId(publicationId);
    }

    @Override
    @Transactional
    public void deletePublicationsByIds(Set<UUID> publicationIds) {
        publicationRepository.deleteByIdIn(publicationIds);
    }
}
