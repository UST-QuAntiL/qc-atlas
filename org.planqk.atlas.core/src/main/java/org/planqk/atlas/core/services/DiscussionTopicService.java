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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for operations related to interacting and modifying {@link DiscussionTopic}s in the database.
 */
public interface DiscussionTopicService {

    @Transactional
    DiscussionTopic create(DiscussionTopic discussionTopic);

    Page<DiscussionTopic> findAll(Pageable pageable);

    Page<DiscussionTopic> findByKnowledgeArtifact(KnowledgeArtifact knowledgeArtifact, Pageable pageable);

    DiscussionTopic findById(UUID topicId);

    @Transactional
    DiscussionTopic update(DiscussionTopic topic);

    @Transactional
    void delete(UUID topicId);
}
