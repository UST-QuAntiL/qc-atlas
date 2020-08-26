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

import org.planqk.atlas.core.model.DiscussionComment;
import org.planqk.atlas.core.repository.DiscussionCommentRepository;
import org.planqk.atlas.core.util.ServiceUtils;

import lombok.AllArgsConstructor;
import lombok.NonNull;
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
    public DiscussionComment create(@NonNull DiscussionComment discussionComment) {
        return discussionCommentRepository.save(discussionComment);
    }

    @Override
    public Page<DiscussionComment> findAll(@NonNull Pageable pageable) {
        return discussionCommentRepository.findAll(pageable);
    }

    @Override
    public Page<DiscussionComment> findAllByTopic(@NonNull UUID topicId, @NonNull Pageable pageable) {
        return discussionCommentRepository.findByDiscussionTopicId(topicId, pageable);
    }

    @Override
    public DiscussionComment findById(@NonNull UUID commentId) {
        return ServiceUtils.findById(commentId, DiscussionComment.class, discussionCommentRepository);
    }

    @Override
    @Transactional
    public DiscussionComment update(@NonNull DiscussionComment comment) {
        ServiceUtils.throwIfNotExists(comment.getId(), DiscussionComment.class, discussionCommentRepository);

        return discussionCommentRepository.save(comment);
    }

    @Override
    @Transactional
    public void delete(@NonNull UUID commentId) {
        ServiceUtils.throwIfNotExists(commentId, DiscussionComment.class, discussionCommentRepository);

        discussionCommentRepository.deleteById(commentId);
    }
}
