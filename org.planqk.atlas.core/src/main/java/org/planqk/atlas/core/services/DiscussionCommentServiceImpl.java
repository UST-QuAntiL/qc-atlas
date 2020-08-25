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

import org.planqk.atlas.core.model.DiscussionComment;
import org.planqk.atlas.core.repository.DiscussionCommentRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class DiscussionCommentServiceImpl implements DiscussionCommentService {

    private final DiscussionCommentRepository discussionCommentRepository;

    @Override
    @Transactional
    public DiscussionComment create(DiscussionComment discussionComment) {
        return discussionCommentRepository.save(discussionComment);
    }

    @Override
    public Page<DiscussionComment> findAll(Pageable pageable) {
        return discussionCommentRepository.findAll(pageable);
    }

    @Override
    public Page<DiscussionComment> findAllByTopic(UUID topicId, Pageable pageable) {
        return discussionCommentRepository.findByDiscussionTopic_Id(topicId, pageable);
    }

    @Override
    public DiscussionComment findById(UUID id) {

        if (!this.existsDiscussionCommentById(id)) {
            throw new NoSuchElementException();
        }
        return discussionCommentRepository.findById(id).get();
    }

    @Override
    @Transactional
    public DiscussionComment update(DiscussionComment comment) {

        if (!this.existsDiscussionCommentById(comment.getId())) {
            throw new NoSuchElementException();
        }
        return discussionCommentRepository.save(comment);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        discussionCommentRepository.deleteById(id);
    }

    @Override
    public boolean existsDiscussionCommentById(UUID id) {

        return discussionCommentRepository.existsById(id);
    }
}
