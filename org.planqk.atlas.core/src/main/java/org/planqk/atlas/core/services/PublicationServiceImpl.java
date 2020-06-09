package org.planqk.atlas.core.services;

import lombok.AllArgsConstructor;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.repository.AlgorithmRepository;
import org.planqk.atlas.core.repository.PublicationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class PublicationServiceImpl implements PublicationService {

	private PublicationRepository publicationRepository;
	private AlgorithmRepository algorithmRepository;

	@Override
	public Publication save(Publication publication) {

		return publicationRepository.save(publication);
	}

	@Override
	public Publication update(UUID id, Publication publication) {

		Optional<Publication> existingPublication = publicationRepository.findById(id);

		if (existingPublication.isPresent()) {
			fillExistingPublication(publication, existingPublication.get());
			return publicationRepository.save(existingPublication.get());
		}else {
		    throw new NoSuchElementException();
        }
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

	@Override
	public Set<Publication> createOrUpdateAll(Set<Publication> publications) {
		Set<Publication> dbPublications = new HashSet<>();

		for (Publication publication : publications) {
			Optional<Publication> optPublication = Objects.isNull(publication.getId()) ? Optional.empty()
					: publicationRepository.findById(publication.getId());

			if (optPublication.isPresent()) {
				// Use existing publication
				fillExistingPublication(publication, optPublication.get());
				dbPublications.add(optPublication.get());
			} else {
				// Create new one if it does not exist and add to algorithm
				dbPublications.add(publicationRepository.save(publication));
			}
		}

		return dbPublications;
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
    public void deletePublicationsByIds(Set<UUID> publicationIds) {
        publicationRepository.deletePublicationsByIds(publicationIds);
    }
}
