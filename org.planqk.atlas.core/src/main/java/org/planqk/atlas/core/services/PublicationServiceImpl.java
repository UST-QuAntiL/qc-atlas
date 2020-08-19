/********************************************************************************
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

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.repository.AlgorithmRepository;
import org.planqk.atlas.core.repository.ImplementationRepository;
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
    private final ImplementationRepository implementationRepository;
    private final AlgorithmRepository algorithmRepository;

    @Override
    public Page<Algorithm> findAlgorithmsOfPublication(UUID publicationId, Pageable p) {
        if (!publicationRepository.existsById(publicationId)) {
            throw new NoSuchElementException("Publication with id " + publicationId.toString() + " Does not exist");
        }
        return algorithmRepository.findAlgorithmsByPublicationId(publicationId, p);
    }

    @Override
    public Page<Implementation> findImplementationsOfPublication(UUID publicationId, Pageable p) {
        if (!publicationRepository.existsById(publicationId)) {
            throw new NoSuchElementException("Publication with id " + publicationId.toString() + " Does not exist");
        }
        return implementationRepository.findImplementationsByPublicationId(publicationId, p);
    }

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
        Publication publication = findById(id);
        publication.getAlgorithms().forEach(algorithm -> algorithm.removePublication(publication));
        publication.getImplementations().forEach(implementation -> implementation.removePublication(publication));
        publicationRepository.deleteById(id);
    }

    @Override
    public Page<Publication> findAll(Pageable pageable, String search) {
        if (!Objects.isNull(search) && !search.isEmpty()) {
            return publicationRepository.findAll(search, pageable);
        }
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
            return new HashSet<>();
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
    @Transactional
    public void deletePublicationsByIds(Set<UUID> publicationIds) {
        publicationRepository.deleteByIdIn(publicationIds);
    }
}
