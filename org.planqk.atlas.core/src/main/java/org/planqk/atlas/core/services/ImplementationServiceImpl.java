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
import java.util.UUID;

import lombok.AllArgsConstructor;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.repository.ImplementationRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ImplementationServiceImpl implements ImplementationService {

    private final ImplementationRepository repository;
    private TagService tagService;
    private PublicationService publicationService;
    private AlgorithmService algorithmService;

    @Override
    public Implementation saveOrUpdate(Implementation implementation) {
        if (implementation.getId() != null) {
            return update(implementation.getId(), implementation);
        }
        return repository.save(implementation);
    }

    @Override
    public Page<Implementation> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Implementation findById(UUID implId) {
        return repository.findById(implId).orElseThrow(NoSuchElementException::new);
    }

    private Implementation update(UUID id, Implementation implementation) {
        Implementation persistetImpl = repository.findById(id).orElseThrow(NoSuchElementException::new);

        persistetImpl.setLink(implementation.getLink());
        persistetImpl.setDependencies(implementation.getDependencies());
        persistetImpl.setParameter(implementation.getParameter());
        persistetImpl.setAssumptions(implementation.getAssumptions());
        persistetImpl.setContributors(implementation.getContributors());
        persistetImpl.setName(implementation.getName());
        persistetImpl.setDescription(implementation.getDescription());
        persistetImpl.setInputFormat(implementation.getInputFormat());
        persistetImpl.setOutputFormat(implementation.getOutputFormat());

        // Tags, Publications, Algorithms
        // update them if new ones are added
        tagService.createOrUpdateAll(implementation.getTags());
        persistetImpl.setTags(implementation.getTags());

        publicationService.createOrUpdateAll(implementation.getPublications());
        persistetImpl.setPublications(implementation.getPublications());

        if (implementation.getImplementedAlgorithm().getId() == null) {
            algorithmService.save(implementation.getImplementedAlgorithm());
        }
        persistetImpl.setImplementedAlgorithm(implementation.getImplementedAlgorithm());

        return repository.save(persistetImpl);
    }
}
