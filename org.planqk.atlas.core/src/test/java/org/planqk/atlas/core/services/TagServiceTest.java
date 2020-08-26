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
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

import org.planqk.atlas.core.model.Tag;
import org.planqk.atlas.core.repository.TagRepository;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
public class TagServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private TagService tagService;
    @Autowired
    private TagRepository tagRepository;

    @Test
    void createTag() {
        var tag = new Tag();
        tag.setValue("Hello");
        tag.setCategory("World");
        tag = tagService.create(tag);

        var dbTag = tagRepository.findById(tag.getValue()).get();
        assertThat(dbTag).isEqualTo(tag);
    }

    @Test
    void findTagById_ElementNotFound() {
        assertThrows(NoSuchElementException.class, () -> {
            tagService.findByValue(UUID.randomUUID().toString());
        });
    }

    @Test
    void findTagById_ElementFound() {
        var tag = new Tag();
        tag.setValue("Hello");
        tag.setCategory("World");
        tag = tagService.create(tag);
        var dbTag = tagService.findByValue(tag.getValue());
        assertThat(dbTag.getValue()).isEqualTo(tag.getValue());
        assertThat(dbTag.getCategory()).isEqualTo(tag.getCategory());
    }

    @Test
    void findAllTags() {
        var tags = new HashSet<Tag>();
        for (int i = 0; i < 10; i++) {
            var tag = new Tag();
            tag.setValue("Hello " + i);
            tag.setCategory("World " + i);
            tags.add(tag);
        }
        var updatedSet = tags.stream().map(e -> tagService.create(e)).collect(Collectors.toSet());
        var dbOutput = tagService.findAll(Pageable.unpaged()).getContent();

        updatedSet.forEach(e -> assertThat(dbOutput).contains(e));
    }
}
