/*******************************************************************************
 * Copyright (c) 2020 the qc-atlas contributors.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.OffsetDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.planqk.atlas.core.model.DiscussionTopic;
import org.planqk.atlas.core.model.KnowledgeArtifact;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.model.Status;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DiscussionTopicServiceTest extends AtlasDatabaseTestBase {

    private final int page = 0;

    private final int size = 2;

    private final Pageable pageable = PageRequest.of(page, size);

    @Autowired
    private DiscussionTopicService topicService;

    @Autowired
    private PublicationService publicationService;

    private KnowledgeArtifact knowledgeArtifact;

    private DiscussionTopic topic;

    private DiscussionTopic topic2;

    @BeforeEach
    public void initialize() {
        topic = new DiscussionTopic();
        topic.setDate(OffsetDateTime.now());
        topic.setTitle("Title");
        topic.setDescription("Description");
        topic.setStatus(Status.CLOSED);
        topic2 = new DiscussionTopic();
        topic2.setDate(OffsetDateTime.now());
        topic2.setTitle("Title");
        topic2.setDescription("Description");
        topic2.setStatus(Status.CLOSED);

        var pub = new Publication();
        pub.setTitle("discussion");
        pub = publicationService.create(pub);

        topic.setKnowledgeArtifact(pub);
        topic2.setKnowledgeArtifact(pub);
        knowledgeArtifact = pub;
    }

    @Test
    void createDiscussionTopic() {
        DiscussionTopic topic = topicService.create(this.topic);
        assertThat(topic.getId()).isNotNull();
        assertThat(topic.getDate()).isEqualTo(this.topic.getDate());
        assertThat(topic.getTitle()).isEqualTo(this.topic.getTitle());
        assertThat(topic.getDescription()).isEqualTo(this.topic.getDescription());
        assertThat(topic.getStatus()).isEqualTo(this.topic.getStatus());
    }

    @Test
    void findAllDiscussionTopics() {
        topicService.create(this.topic);
        topicService.create(this.topic2);

        Page<DiscussionTopic> discussionTopicPage = topicService.findAll(pageable);
        assertThat(discussionTopicPage.getTotalElements()).isEqualTo(2);
    }

    // @Test
    void findDiscussionTopicById_ElementFound() {
        // TODO
    }

    @Test
    void findDiscussionTopicById_ElementNotFound() {
        assertThrows(NoSuchElementException.class, () -> {
            topicService.findById(UUID.randomUUID());
        });
    }

    @Test
    void updateDiscussionTopic_ElementFound() {
        DiscussionTopic topic = topicService.create(this.topic);
        topic.setTitle("New Title");
        DiscussionTopic update = topicService.update(topic);

        assertThat(update.getDate()).isEqualTo(topic.getDate());
        assertThat(update.getTitle()).isEqualTo(topic.getTitle());
        assertThat(update.getStatus()).isEqualTo(topic.getStatus());
    }

    @Test
    void updateDiscussionTopic_ElementNotFound() {
        topic.setId(UUID.randomUUID());
        assertThrows(NoSuchElementException.class, () -> {
            topicService.update(this.topic);
        });
    }

    @Test
    void deleteDiscussionTopic_ElementFound() {
        DiscussionTopic topic = topicService.create(this.topic);
        topicService.delete(topic.getId());
        assertThrows(NoSuchElementException.class, () -> {
            topicService.findById(topic.getId());
        });
    }

    // @Test
    void deleteDiscussionTopic_ElementNotFound() {
        // TODO
    }

    @Test
    void findByKnowledgeArtifact() {
        topicService.create(topic);
        topicService.create(topic2);

        var page = topicService.findByKnowledgeArtifact(knowledgeArtifact, Pageable.unpaged());
        assertThat(page.getTotalElements()).isEqualTo(2);
    }

    @Test
    void findByKnowledgeArtifactId() {
        topicService.create(topic);
        topicService.create(topic2);

        var page = topicService.findByKnowledgeArtifactId(knowledgeArtifact.getId(), Pageable.unpaged());
        assertThat(page.getTotalElements()).isEqualTo(2);
    }

    @Test
    void checkIfDiscussionTopicIsLinkedToKnowledgeArtifact() {
        var pub = new Publication();
        pub.setTitle("discussion");
        pub = publicationService.create(pub);
        topic.setKnowledgeArtifact(pub);
        topic = topicService.create(topic);
        topicService.checkIfDiscussionTopicIsLinkedToKnowledgeArtifact(topic.getId(), pub.getId());

        assertThrows(NoSuchElementException.class, () -> {
            var pub2 = new Publication();
            pub2.setTitle("discussion2");
            pub2 = publicationService.create(pub2);
            topicService.checkIfDiscussionTopicIsLinkedToKnowledgeArtifact(topic.getId(), pub2.getId());
        });
    }
}
