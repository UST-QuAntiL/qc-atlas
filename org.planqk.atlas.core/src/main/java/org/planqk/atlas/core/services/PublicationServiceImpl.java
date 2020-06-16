/*******************************************************************************
 * Copyright (c) 2020 University of Stuttgart
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
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

		if(publications == null) {
		    return dbPublications;
        }

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

}
