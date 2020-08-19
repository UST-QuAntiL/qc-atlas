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

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.model.SoftwarePlatform;
import org.planqk.atlas.core.repository.AlgorithmRepository;
import org.planqk.atlas.core.repository.ImplementationRepository;
import org.planqk.atlas.core.repository.PublicationRepository;
import org.planqk.atlas.core.repository.SoftwarePlatformRepository;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ImplementationServiceImpl implements ImplementationService {

    private final ImplementationRepository implementationRepository;
    private final SoftwarePlatformRepository softwarePlatformRepository;
    //    private final TagService tagService;
    private final PublicationRepository publicationRepository;
    private final AlgorithmService algorithmService;
    private final AlgorithmRepository algorithmRepository;

    @Override
    @Transactional
    public Implementation save(Implementation implementation) {
        return implementationRepository.save(implementation);
    }

    @Override
    @Transactional
    public Implementation create(Implementation implementation, UUID implementedAlgorithmId) {
        Algorithm implementedAlgorithm = algorithmService.findById(implementedAlgorithmId);
        implementation.setImplementedAlgorithm(implementedAlgorithm);
        return implementationRepository.save(implementation);
    }

    @Override
    public Page<Implementation> findAll(Pageable pageable) {
        return this.implementationRepository.findAll(pageable);
    }

    @Override
    public Implementation findById(UUID implementationId) {
        return implementationRepository.findById(implementationId).orElseThrow(NoSuchElementException::new);
    }

    @Override
    @Transactional
    public Implementation update(UUID implementationId, Implementation implementation) {
        Implementation persistedImplementation = findById(implementationId);

        persistedImplementation.setName(implementation.getName());
        persistedImplementation.setDescription(implementation.getDescription());
        persistedImplementation.setContributors(implementation.getContributors());
        persistedImplementation.setAssumptions(implementation.getAssumptions());
        persistedImplementation.setInputFormat(implementation.getInputFormat());
        persistedImplementation.setParameter(implementation.getParameter());
        persistedImplementation.setOutputFormat(implementation.getOutputFormat());
        persistedImplementation.setLink(implementation.getLink());
        persistedImplementation.setDependencies(implementation.getDependencies());

        return implementationRepository.save(persistedImplementation);
    }

    @Override
    @Transactional
    public void delete(UUID implementationId) {
        Implementation implementation = findById(implementationId);

        removeReferences(implementation);

        implementationRepository.deleteById(implementationId);
    }

    private void removeReferences(Implementation implementation) {
        // TODO rethink the model implementation
        implementation.setImplementedAlgorithm(null);

        implementation.getPublications().forEach(publication ->
                publication.removeImplementation(implementation));

        implementation.getSoftwarePlatforms().forEach(softwarePlatform ->
                softwarePlatform.removeImplementation(implementation));
    }

    @Override
    public Page<Implementation> findByImplementedAlgorithm(UUID algorithmId, Pageable pageable) {
        if (!algorithmRepository.existsAlgorithmById(algorithmId)) {
            throw new NoSuchElementException("Algorithm with ID \"" + algorithmId + "\" does not exist");
        }

        return implementationRepository.findByImplementedAlgorithmId(algorithmId, pageable);
    }

    @Override
    public Algorithm getImplementedAlgorithm(UUID implementationId) {
        return findById(implementationId).getImplementedAlgorithm();
    }

    @Override
    public Page<SoftwarePlatform> findLinkedSoftwarePlatforms(UUID implementationId, Pageable pageable) {
        return softwarePlatformRepository.findSoftwarePlatformsByImplementationId(implementationId, pageable);
    }

    @Override
    public Page<Publication> findLinkedPublications(UUID implementationId, Pageable pageable) {
        return publicationRepository.findPublicationsByImplementationId(implementationId, pageable);
    }
}
