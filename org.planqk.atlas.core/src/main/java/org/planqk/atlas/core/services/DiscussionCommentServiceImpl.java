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
import java.util.Optional;
import java.util.UUID;

import org.planqk.atlas.core.model.DiscussionComment;
import org.planqk.atlas.core.model.DiscussionTopic;
import org.planqk.atlas.core.repository.DiscussionCommentRepository;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class DiscussionCommentServiceImpl implements DiscussionCommentService {

    private DiscussionCommentRepository repository;
    private DiscussionTopicService discussionTopicService;

    @Override
    public DiscussionComment save(DiscussionComment discussionComment) {

        Optional<DiscussionTopic> discussionTopic = Optional.of(discussionTopicService.findById(discussionComment.getDiscussionTopic().getId()));
        if (discussionTopic.isEmpty()) {
            throw new NoSuchElementException("The referenced Discussion Topic does not exist!");
        }
        return repository.save(discussionComment);
    }

    @Override
    public Page<DiscussionComment> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public DiscussionComment findById(UUID id) {

        return repository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public DiscussionComment update(UUID id, DiscussionComment comment) {
        repository.findById(id).orElseThrow(NoSuchElementException::new);
        return repository.save(comment);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
