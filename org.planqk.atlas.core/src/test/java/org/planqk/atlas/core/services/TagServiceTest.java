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

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.model.ClassicImplementation;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.Tag;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.planqk.atlas.core.util.ServiceTestUtils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
public class TagServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private TagService tagService;
    @Autowired
    private AlgorithmService algorithmService;
    @Autowired
    private ImplementationService implementationService;

    @Test
    void createTag() {
        var tag = new Tag();
        tag.setValue("value");
        tag.setCategory("category");

        var storedTag = tagService.create(tag);

        assertThat(storedTag).isEqualTo(tag);
    }

    @Test
    void findAllTags() {
        var tags = new HashSet<Tag>();
        for (int i = 0; i < 10; i++) {
            var tag = new Tag();
            tag.setValue("value " + i);
            tag.setCategory("category " + i);
            tags.add(tagService.create(tag));
        }

        var allPersistedTags = tagService.findAll(Pageable.unpaged()).getContent();

        ServiceTestUtils.assertCollectionEquality(allPersistedTags, tags);
    }

    @Test
    void findAllTagsByContent() {
        var tags = new HashSet<Tag>();
        for (int i = 0; i < 10; i++) {
            var tag = new Tag();
            tag.setValue("value " + i);
            tag.setCategory("category " + i);
            tags.add(tagService.create(tag));
        }

        var filteredTags = tags.stream().filter(e -> e.getCategory().contains("1") || e.getValue().contains("1"))
                .collect(Collectors.toSet());
        var searchedTags = tagService.findAllByContent("1", Pageable.unpaged()).getContent();

        assertThat(searchedTags.size()).isEqualTo(1);
        ServiceTestUtils.assertCollectionEquality(filteredTags, searchedTags);
    }

    @Test
    void findAllTagsByCategory() {
        var tags = new HashSet<Tag>();
        for (int i = 0; i < 10; i++) {
            var tag = new Tag();
            tag.setValue("value " + i);
            if (i % 2 == 0) {
                tag.setCategory("categorySearch");
            } else {
                tag.setCategory("category" + i);
            }
            tags.add(tagService.create(tag));
        }

        var filteredTags = tags.stream().filter(e -> e.getCategory().contains("categorySearch")).collect(Collectors.toSet());
        var searchedTags = tagService.findAllByCategory("categorySearch", Pageable.unpaged()).getContent();

        assertThat(searchedTags.size()).isEqualTo(5);
        ServiceTestUtils.assertCollectionEquality(filteredTags, searchedTags);
    }

    @Test
    void findTagByValue_ElementNotFound() {
        assertThrows(NoSuchElementException.class, () -> tagService.findByValue(UUID.randomUUID().toString()));
    }

    @Test
    void findTagByValue_ElementFound() {
        var tag = new Tag();
        tag.setValue("value");
        tag.setCategory("category");

        tag = tagService.create(tag);

        var dbTag = tagService.findByValue(tag.getValue());

        assertThat(dbTag.getValue()).isEqualTo(tag.getValue());
        assertThat(dbTag.getCategory()).isEqualTo(tag.getCategory());
    }

    @Test
    void addTagToAlgorithm() {
        Algorithm algorithm = new ClassicAlgorithm();
        algorithm.setName("algorithmName");
        algorithm = algorithmService.create(algorithm);

        var tag1 = new Tag();
        tag1.setValue("value1");
        tag1.setCategory("category1");

        tagService.addTagToAlgorithm(algorithm.getId(), tag1);

        var tag2 = new Tag();
        tag2.setValue("value2");
        tag2.setCategory("category2");
        tag2 = tagService.create(tag2);

        tagService.addTagToAlgorithm(algorithm.getId(), tag2);

        var finalAlgorithm = algorithmService.findById(algorithm.getId());
        var tagsOfAlgorithm = finalAlgorithm.getTags();
        assertThat(tagsOfAlgorithm.size()).isEqualTo(2);
        assertThat(tagsOfAlgorithm.contains(tag1)).isTrue();
        assertThat(tagsOfAlgorithm.contains(tag2)).isTrue();
    }

    @Test
    void removeTagFromAlgorithm() {
        Algorithm algorithm = new ClassicAlgorithm();
        algorithm.setName("algorithmName");
        algorithm = algorithmService.create(algorithm);

        var tag = new Tag();
        tag.setValue("value1");
        tag.setCategory("category1");

        tagService.addTagToAlgorithm(algorithm.getId(), tag);

        assertDoesNotThrow(() -> tagService.findByValue(tag.getValue()));

        tagService.removeTagFromAlgorithm(algorithm.getId(), tag);

        assertDoesNotThrow(() -> tagService.findByValue(tag.getValue()));
        var tagsOfAlgorithm = algorithmService.findById(algorithm.getId()).getTags();

        assertThat(tagsOfAlgorithm.size()).isEqualTo(0);
    }

    @Test
    void addTagToImplementation() {
        Algorithm algorithm = new ClassicAlgorithm();
        algorithm.setName("algorithmName");
        algorithm = algorithmService.create(algorithm);

        Implementation implementation = new ClassicImplementation();
        implementation.setName("implementationName");
        implementation = implementationService.create(implementation, algorithm.getId());

        var tag1 = new Tag();
        tag1.setValue("value1");
        tag1.setCategory("category1");

        tagService.addTagToImplementation(implementation.getId(), tag1);

        var tag2 = new Tag();
        tag2.setValue("value2");
        tag2.setCategory("category2");
        tag2 = tagService.create(tag2);

        tagService.addTagToImplementation(implementation.getId(), tag2);

        var finalImplementation = implementationService.findById(implementation.getId());
        var tagsOfImplementation = finalImplementation.getTags();
        assertThat(tagsOfImplementation.size()).isEqualTo(2);
        assertThat(tagsOfImplementation.contains(tag1)).isTrue();
        assertThat(tagsOfImplementation.contains(tag2)).isTrue();
    }

    @Test
    void removeTagFromImplementation() {
        Algorithm algorithm = new ClassicAlgorithm();
        algorithm.setName("algorithmName");
        algorithm = algorithmService.create(algorithm);

        Implementation implementation = new ClassicImplementation();
        implementation.setName("implementationName");
        implementation = implementationService.create(implementation, algorithm.getId());

        var tag = new Tag();
        tag.setValue("value1");
        tag.setCategory("category1");

        tagService.addTagToImplementation(implementation.getId(), tag);

        assertDoesNotThrow(() -> tagService.findByValue(tag.getValue()));

        tagService.removeTagFromImplementation(implementation.getId(), tag);

        assertDoesNotThrow(() -> tagService.findByValue(tag.getValue()));
        var tagsOfImplementation = implementationService.findById(implementation.getId()).getTags();

        assertThat(tagsOfImplementation.size()).isEqualTo(0);
    }
}
