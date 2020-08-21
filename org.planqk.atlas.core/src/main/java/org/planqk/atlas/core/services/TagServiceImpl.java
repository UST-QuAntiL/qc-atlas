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

import java.util.UUID;

import lombok.AllArgsConstructor;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.Tag;
import org.planqk.atlas.core.repository.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    private final AlgorithmService algorithmService;

    private final ImplementationService implementationService;

    @Override
    @Transactional
    public Tag save(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    public Page<Tag> findAll(Pageable pageable) {
        return tagRepository.findAll(pageable);
    }

    @Override
    public Page<Tag> findAllByContent(String search, Pageable pageable) {
        return tagRepository.findByValueContainingIgnoreCaseOrCategoryContainingIgnoreCase(search, search, pageable);
    }

    @Override
    public Page<Tag> findAllByCategory(String category, Pageable pageable) {
        return tagRepository.findByCategory(category, pageable);
    }

    @Override
    public Tag findByValue(String value) {
        return tagRepository.findByValue(value);
    }

    @Override
    @Transactional
    public void addTagToAlgorithm(UUID algorithmId, Tag tag) {
        Algorithm algorithm = algorithmService.findById(algorithmId);

        algorithm.addTag(createTagIfNotExists(tag));
    }

    @Override
    @Transactional
    public void removeTagFromAlgorithm(UUID algorithmId, Tag tag) {
        Algorithm algorithm = algorithmService.findById(algorithmId);

        algorithm.removeTag(tag);
    }

    @Override
    @Transactional
    public void addTagToImplementation(UUID implementationId, Tag tag) {
        Implementation implementation = implementationService.findById(implementationId);

        implementation.addTag(createTagIfNotExists(tag));
    }

    @Override
    @Transactional
    public void removeTagFromImplementation(UUID implementationId, Tag tag) {
        Implementation implementation = implementationService.findById(implementationId);

        implementation.removeTag(tag);
    }

    private Tag createTagIfNotExists(Tag tag) {
        if (tagRepository.existsTagByValue(tag.getValue())) {
            return tag;
        }
        return save(tag);
    }
}

