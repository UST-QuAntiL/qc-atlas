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

import java.util.List;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class ImplementationServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private ImplementationService implementationService;

    @Autowired
    private AlgorithmService algorithmService;

//    @Autowired
//    private TagService tagService;
    //    @Autowired
//    private TagRepository tagRepository;

    @Test
    void testAddImplementation() {
        // Tags will be used/tested and included in the future
//        var tag = new Tag();
//        tag.setKey("test");
//        tag.setValue("test");
//        Set<Tag> tags = new HashSet<>();
//        tags.add(tag);

        Algorithm algo = new ClassicAlgorithm();
        algo.setName("test");
        algo = algorithmService.create(algo);

        var impl = new Implementation();
        impl.setName("test-impl");
        impl.setImplementedAlgorithm(algo);
//        impl.setTags(tags);
//        tags.forEach(e -> e.setImplementations(SetUtils.hashSetOf(impl)));

        var returnedImpl = implementationService.create(impl, algo.getId());

//        var returnedTag = tagService.getTagById(tag.getId());
//        returnedTag.setImplementations(SetUtils.hashSetOf(impl));
//        returnedImpl.setTags(SetUtils.hashSetOf(tag));

        implementationService.create(returnedImpl, algo.getId());

        var dbImpl = implementationService.findById(returnedImpl.getId());
        assertThat(dbImpl.getName()).isEqualTo(impl.getName());
        assertThat(dbImpl.getImplementedAlgorithm().getName()).isEqualTo(algo.getName());
//        assertThat(dbImpl.getTags().size()).isEqualTo(1);
    }

    @Test
    void testFindByImplementedAlgorithm() {
        Algorithm algo = new Algorithm();
        algo.setName("dummy");
        algo = algorithmService.create(algo);

        Implementation implementation1 = new Implementation();
        implementation1.setName("test-impl1");
        implementation1.setImplementedAlgorithm(algo);
        implementationService.create(implementation1, algo.getId());
        Implementation implementation2 = new Implementation();
        implementation2.setName("test-impl2");
        implementation2.setImplementedAlgorithm(algo);
        implementationService.create(implementation2, algo.getId());

        List<Implementation> implementations = implementationService.findByImplementedAlgorithm(algo.getId(), Pageable.unpaged()).getContent();

        assertThat(implementations.size()).isEqualTo(2);
    }
}
