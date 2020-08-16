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

import org.planqk.atlas.core.model.DiscussionComment;
import org.planqk.atlas.core.model.DiscussionTopic;
import org.planqk.atlas.core.model.KnowledgeArtifact;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DiscussionCommentServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private DiscussionTopicService topicService;
    @Autowired
    private DiscussionCommentService commentService;
    @Autowired
    private PublicationService publicationService;

    private DiscussionComment comment;
    private DiscussionComment comment2;
    private DiscussionTopic topic;

    private final int page = 0;
    private final int size = 2;

    private final Pageable pageable = PageRequest.of(page, size);

    @BeforeEach
    public void initialize() throws Exception {
        var pub = PublicationServiceTest.getGenericTestPublication("discussion");
        pub = publicationService.save(pub);

        topic = new DiscussionTopic();
        topic.setKnowledgeArtifact(pub);

        comment = new DiscussionComment();
        comment.setDate(OffsetDateTime.now());
        comment.setText("Test Text");
        comment.setDiscussionTopic(topic);

        comment2 = new DiscussionComment();
        comment2.setDate(OffsetDateTime.now());
        comment2.setText("Test Text");
        comment2.setDiscussionTopic(topic);
    }

    @Test
    void createDiscussionComment() {
        topicService.save(this.topic);
        DiscussionComment comment = commentService.save(this.comment);
        assertThat(comment.getId()).isNotNull();
        assertThat(comment.getDate()).isEqualTo(this.comment.getDate());
        assertThat(comment.getText()).isEqualTo(this.comment.getText());
        assertThat(comment.getDiscussionTopic()).isEqualTo(this.comment.getDiscussionTopic());
    }

    @Test
    void updateDiscussionComment() {
        topicService.save(this.topic);
        DiscussionComment comment = commentService.save(this.comment);
        comment.setText("New Text");
        DiscussionComment update = commentService.update(comment.getId(), comment);

        assertThat(update.getDate()).isEqualTo(comment.getDate());
        assertThat(update.getText()).isEqualTo(comment.getText());
        assertThat(update.getDiscussionTopic()).isEqualTo(comment.getDiscussionTopic());
    }

    @Test
    void updateDiscussionComment_notFound() {
        assertThrows(NoSuchElementException.class, () -> {
            commentService.update(UUID.randomUUID(), comment);
        });
    }

    @Test
    void findDiscussionCommentById_notFound() {
        assertThrows(NoSuchElementException.class, () -> {
            commentService.findById(UUID.randomUUID());
        });
    }

    @Test
    void findAllDiscussionComments() {
        topicService.save(this.topic);
        commentService.save(this.comment);
        commentService.save(this.comment2);

        Page<DiscussionComment> discussionCommentPage = commentService.findAll(pageable);
        assertThat(discussionCommentPage.getTotalElements()).isEqualTo(2);
    }

    @Test
    void deleteDiscussionComment() {
        topicService.save(this.topic);
        DiscussionComment comment = commentService.save(this.comment);
        commentService.deleteById(comment.getId());
        assertThrows(NoSuchElementException.class, () -> {
            commentService.findById(comment.getId());
        });
    }

    @Test
    void existsDiscussionComment_exists(){
        topicService.save(this.topic);
        DiscussionComment comment = commentService.save(this.comment);
        boolean exists = commentService.existsDiscussionCommentById(comment.getId());

        assertThat(exists).isEqualTo(true);
    }

    @Test
    void existsDiscussionComment_notExists(){

        boolean exists = commentService.existsDiscussionCommentById(UUID.randomUUID());
        assertThat(exists).isEqualTo(false);
    }
}
