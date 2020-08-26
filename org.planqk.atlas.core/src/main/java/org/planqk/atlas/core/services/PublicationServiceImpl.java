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

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.repository.AlgorithmRepository;
import org.planqk.atlas.core.repository.ImplementationRepository;
import org.planqk.atlas.core.repository.PublicationRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class PublicationServiceImpl implements PublicationService {

    private final PublicationRepository publicationRepository;
    private final ImplementationRepository implementationRepository;
    private final AlgorithmRepository algorithmRepository;

    @Override
    @Transactional
    public Publication create(Publication publication) {
        return publicationRepository.save(publication);
    }

    @Override
    public Page<Publication> findAll(Pageable pageable, String search) {
        if (!Objects.isNull(search) && !search.isEmpty()) {
            return publicationRepository.findAll(search, pageable);
        }
        return publicationRepository.findAll(pageable);
    }

    @Override
    public Publication findById(UUID publicationId) {
        return publicationRepository.findById(publicationId).orElseThrow(NoSuchElementException::new);
    }

    @Override
    @Transactional
    public Publication update(Publication publication) {
        var persistedPublication = findById(publication.getId());

        persistedPublication.setTitle(publication.getTitle());
        persistedPublication.setDoi(publication.getDoi());
        persistedPublication.setUrl(publication.getUrl());
        persistedPublication.setAuthors(publication.getAuthors());

        return publicationRepository.save(persistedPublication);
    }

    @Override
    @Transactional
    public void delete(UUID publicationId) {
        Publication publication = findById(publicationId);

        removeReferences(publication);

        publicationRepository.deleteById(publicationId);
    }

    private void removeReferences(Publication publication) {
        publication.getAlgorithms().forEach(algorithm -> algorithm.removePublication(publication));

        publication.getImplementations().forEach(implementation -> implementation.removePublication(publication));
    }

    @Override
    public Page<Algorithm> findAlgorithmsOfPublication(UUID publicationId, Pageable pageable) {
        if (!publicationRepository.existsById(publicationId)) {
            throw new NoSuchElementException("Publication with id " + publicationId.toString() + " Does not exist");
        }
        return algorithmRepository.findAlgorithmsByPublicationId(publicationId, pageable);
    }

    @Override
    public Page<Implementation> findImplementationsOfPublication(UUID publicationId, Pageable pageable) {
        if (!publicationRepository.existsById(publicationId)) {
            throw new NoSuchElementException("Publication with id " + publicationId.toString() + " Does not exist");
        }
        return implementationRepository.findImplementationsByPublicationId(publicationId, pageable);
    }

    @Override
    @Transactional
    public void deletePublications(Set<UUID> publicationIds) {
        publicationRepository.deleteByIdIn(publicationIds);
    }
}
