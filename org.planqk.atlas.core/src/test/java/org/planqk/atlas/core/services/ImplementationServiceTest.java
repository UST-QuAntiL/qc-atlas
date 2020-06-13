/********************************************************************************
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

import java.util.Set;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.Tag;
import org.planqk.atlas.core.repository.TagRepository;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class ImplementationServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private ImplementationService implementationService;

    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private TagService tagService;

    @Autowired
    private TagRepository tagRepository;

    @Test
    void testAddImplementation() {
        var tag = new Tag();
        tag.setKey("test");
        tag.setValue("test");
        tag = tagService.save(tag);

        Algorithm algo = new ClassicAlgorithm();
        algo.setName("test");
        algo = algorithmService.save(algo);

        var impl = new Implementation();
        impl.setName("test-impl");
        impl.setImplementedAlgorithm(algo);
//        impl.setTags(tags);
//        tags.forEach(e -> e.setImplementations(Set.of(impl)));

        var returnedImpl = implementationService.save(impl);

        var returnedTag = tagService.getTagById(tag.getId());
        returnedTag.setImplementations(Set.of(returnedImpl));
        returnedImpl.setTags(Set.of(returnedTag));

        implementationService.save(returnedImpl);

        var dbImpl = implementationService.findById(returnedImpl.getId());
        assertThat(dbImpl.getName()).isEqualTo(impl.getName());
        assertThat(dbImpl.getImplementedAlgorithm().getName()).isEqualTo(algo.getName());
        assertThat(dbImpl.getTags().size()).isEqualTo(1);
    }
}
