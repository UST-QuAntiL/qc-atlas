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

import java.time.OffsetDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.planqk.atlas.core.model.DiscussionTopic;
import org.planqk.atlas.core.model.Status;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DiscussionTopicServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private DiscussionTopicService topicService;

    private DiscussionTopic topic;
    private DiscussionTopic topic2;

    private final int page = 0;
    private final int size = 2;

    private final Pageable pageable = PageRequest.of(page, size);

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
    }

    @Test
    void createDiscussionTopic() {
        DiscussionTopic topic = topicService.save(this.topic);
        assertThat(topic.getId()).isNotNull();
        assertThat(topic.getDate()).isEqualTo(this.topic.getDate());
        assertThat(topic.getTitle()).isEqualTo(this.topic.getTitle());
        assertThat(topic.getDescription()).isEqualTo(this.topic.getDescription());
        assertThat(topic.getStatus()).isEqualTo(this.topic.getStatus());
    }

    @Test
    void updateDiscussionTopic() {
        DiscussionTopic topic = topicService.save(this.topic);
        topic.setTitle("New Title");
        DiscussionTopic update = topicService.update(topic.getId(), topic);

        assertThat(update.getDate()).isEqualTo(topic.getDate());
        assertThat(update.getTitle()).isEqualTo(topic.getTitle());
        assertThat(update.getStatus()).isEqualTo(topic.getStatus());
    }

    @Test
    void updateDiscussionTopic_notFound() {
        assertThrows(NoSuchElementException.class, () -> {
            topicService.update(UUID.randomUUID(), this.topic);
        });
    }

    @Test
    void findDiscussionTopicById_notFound() {
        assertThrows(NoSuchElementException.class, () -> {
            topicService.findById(UUID.randomUUID());
        });
    }

    @Test
    void findAllDiscussionTopic() {
        topicService.save(this.topic);
        topicService.save(this.topic2);

        Page<DiscussionTopic> discussionTopicPage = topicService.findAll(pageable);
        assertThat(discussionTopicPage.getTotalElements()).isEqualTo(2);
    }

    @Test
    void deleteDiscussionTopic() {
        DiscussionTopic topic = topicService.save(this.topic);
        topicService.deleteById(topic.getId());
        assertThrows(NoSuchElementException.class, () -> {
            topicService.findById(topic.getId());
        });
    }

    @Test
    void existsDiscussionTopic_exists(){
        DiscussionTopic topic = topicService.save(this.topic);
        boolean exists = topicService.existsDiscussionTopicById(topic.getId());

        assertThat(exists).isEqualTo(true);
    }

    @Test
    void existsDiscussionTopic_notExists(){

        boolean exists = topicService.existsDiscussionTopicById(UUID.randomUUID());
        assertThat(exists).isEqualTo(false);
    }
}
