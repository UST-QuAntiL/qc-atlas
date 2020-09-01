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

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import org.planqk.atlas.core.model.Tag;
import org.planqk.atlas.core.repository.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class TagServiceImpl implements TagService {

    private TagRepository tagRepository;

    @Override
    public Tag findByName(String value) {
        return tagRepository.findByValue(value);
    }

    @Override
    public Set<Tag> findByCategory(String category) {
        return tagRepository.findByCategory(category);
    }

    @Override
    public Page<Tag> findAllByContent(String search, Pageable pageable) {
        return tagRepository.findByValueContainingIgnoreCaseOrCategoryContainingIgnoreCase(search, search, pageable);
    }

    @Transactional
    @Override
    public Tag save(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    public Page<Tag> findAll(Pageable pageable) {
        return tagRepository.findAll(pageable);
    }

    @Transactional
    @Override
    public Set<Tag> createOrUpdateAll(Set<Tag> algorithmTags) {
        return algorithmTags.stream().map(this::save).collect(Collectors.toSet());
    }
}

