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

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.planqk.atlas.core.model.Tag;
import org.planqk.atlas.core.repository.TagRepository;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TagServiceImpl implements TagService {

    private TagRepository tagRepository;


    @Override
    public List<Tag> findByName(String key) {
        return tagRepository.findByKey(key);
    }

    @Override
    public Tag save(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    public Page<Tag> findAll(Pageable pageable) {
        return tagRepository.findAll(pageable);
    }

    @Override
    public Tag getTagById(UUID tagId) {
        return tagRepository.findById(tagId).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Set<Tag> createOrUpdateAll(Set<Tag> algorithmTags) {
        Set<Tag> tags = new HashSet<>();
        // Go Iterate all tags
        for (Tag tag : algorithmTags) {
            // Check for tag in database
            Optional<Tag> optTag = Objects.isNull(tag.getId()) ? Optional.empty() : tagRepository.findById(tag.getId());
            if (optTag.isPresent()) {
                Tag persistedTag = optTag.get();
                tags.add(persistedTag);
            } else {
                // If Tag does not exist --> Create one
                tags.add(save(tag));
            }
        }

        return tags;
    }
}
