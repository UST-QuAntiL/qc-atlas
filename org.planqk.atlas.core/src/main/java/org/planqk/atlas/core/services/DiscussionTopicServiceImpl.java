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

import org.planqk.atlas.core.model.DiscussionTopic;
import org.planqk.atlas.core.model.KnowledgeArtifact;
import org.planqk.atlas.core.repository.DiscussionTopicRepository;
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
public class DiscussionTopicServiceImpl implements DiscussionTopicService {

    private final DiscussionTopicRepository discussionTopicRepository;

    @Override
    @Transactional
    public DiscussionTopic create(@NonNull DiscussionTopic discussionTopic) {
        return discussionTopicRepository.save(discussionTopic);
    }

    @Override
    public Page<DiscussionTopic> findAll(@NonNull Pageable pageable) {
        return discussionTopicRepository.findAll(pageable);
    }

    @Override
    public Page<DiscussionTopic> findByKnowledgeArtifact(
            @NonNull KnowledgeArtifact knowledgeArtifact, @NonNull Pageable pageable) {
        return discussionTopicRepository.findByKnowledgeArtifact(knowledgeArtifact, pageable);
    }

    @Override
    public DiscussionTopic findById(@NonNull UUID topicId) {
        return ServiceUtils.findById(topicId, DiscussionTopic.class, discussionTopicRepository);
    }

    @Override
    @Transactional
    public DiscussionTopic update(@NonNull DiscussionTopic topic) {
        ServiceUtils.throwIfNotExists(topic.getId(), DiscussionTopic.class, discussionTopicRepository);
        return discussionTopicRepository.save(topic);
    }

    @Override
    @Transactional
    public void delete(@NonNull UUID topicId) {
        ServiceUtils.throwIfNotExists(topicId, DiscussionTopic.class, discussionTopicRepository);

        discussionTopicRepository.deleteById(topicId);
    }

}
