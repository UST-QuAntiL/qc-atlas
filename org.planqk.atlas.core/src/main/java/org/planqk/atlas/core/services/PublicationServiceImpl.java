package org.planqk.atlas.core.services;

import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.model.exceptions.NotFoundException;
import org.planqk.atlas.core.repository.PublicationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class PublicationServiceImpl implements PublicationService {

    private final PublicationRepository publicationRepository;

    public PublicationServiceImpl(PublicationRepository publicationRepository) {
        this.publicationRepository=publicationRepository;
    }

    @Override
    public Publication save(Publication publication) {

        return publicationRepository.save(publication);
    }

    @Override
    public Publication update(UUID id, Publication publication) {

        Optional<Publication> existingPublication = publicationRepository.findById(id);

        if(existingPublication.isPresent()){
            Publication persistedPublication = existingPublication.get();
            persistedPublication.setTitle(publication.getTitle());
            persistedPublication.setDoi(publication.getDoi());
            persistedPublication.setUrl(publication.getUrl());
            persistedPublication.setAuthors(publication.getAuthors());
            return publicationRepository.save(persistedPublication);
        }
        return null;
    }

    @Override
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
}
