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

import org.planqk.atlas.core.util.AtlasDatabaseTestBase;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TagServiceTest extends AtlasDatabaseTestBase {

//    @Autowired
//    private TagService tagService;
//
//    @Autowired
//    private TagRepository tagRepository;
//
////    @Test
////    void testAddTag() {
////        var tag = new Tag();
////        tag.setValue("Hello");
////        tag.setKey("World");
////        tag = tagService.save(tag);
////
////        var dbTag = tagRepository.findById(tag.getId()).get();
////        assertThat(dbTag).isEqualTo(tag);
////    }
//
//    @Test
//    void testFindTagById_ElementNotFound() {
//        assertThrows(NoSuchElementException.class, () -> {
//            tagService.getTagById(UUID.randomUUID());
//        });
//    }
//
//    @Test
//    void testFindTagById_ElementFound() {
//        var tag = new Tag();
//        tag.setValue("Hello");
//        tag.setKey("World");
//        tag = tagService.save(tag);
//
//        var dbTag = tagService.getTagById(tag.getId());
//        assertThat(dbTag.getId()).isEqualTo(tag.getId());
//        assertThat(dbTag.getValue()).isEqualTo(tag.getValue());
//        assertThat(dbTag.getKey()).isEqualTo(tag.getKey());
//    }
//
//    @Test
//    void testAddOrUpdateMany_AddAll() {
//        var tags = new HashSet<Tag>();
//        for (int i = 0; i < 10; i++) {
//            var tag = new Tag();
//            tag.setValue("Hello " + i);
//            tag.setKey("World " + i);
//            tags.add(tag);
//        }
//        var updatedSet = tagService.createOrUpdateAll(tags);
//        assertTagListIdsPresent(updatedSet, tags);
//        assertTagListEquality(updatedSet, tags);
//    }
//
//    @Test
//    void testAddOrUpdateMany_AddHalf_UpdateHalf() {
//        var tags = new HashSet<Tag>();
//        for (int i = 0; i < 10; i++) {
//            var tag = new Tag();
//            tag.setValue("Hello " + i);
//            tag.setKey("World " + i);
//            if (i % 2 == 0) {
//                tags.add(tag);
//            } else {
//                tags.add(tagService.save(tag));
//            }
//        }
//        var updatedSet = tagService.createOrUpdateAll(tags);
//        assertTagListIdsPresent(updatedSet, tags);
//        assertTagListEquality(updatedSet, tags);
//    }
//
//    @Test
//    void testFindAll() {
//        var tags = new HashSet<Tag>();
//        for (int i = 0; i < 10; i++) {
//            var tag = new Tag();
//            tag.setValue("Hello " + i);
//            tag.setKey("World " + i);
//            tags.add(tag);
//        }
//        var updatedSet = tagService.createOrUpdateAll(tags);
//        var dbOutput = tagService.findAll(Pageable.unpaged()).getContent();
//        assertTagListIdsPresent(updatedSet, dbOutput);
//        assertTagListEquality(updatedSet, dbOutput);
//    }
//
//    private void assertTagListIdsPresent(Collection<Tag> a, Collection<Tag> b) {
//        assertThat(a.stream().filter(e -> e.getId() != null).count()).isEqualTo(b.size());
//    }
//
//    private void assertTagListEquality(Collection<Tag> a, Collection<Tag> b) {
//        assertThat(a.size()).isEqualTo(b.size());
//        assertThat(a.stream().filter(e -> b.stream().anyMatch(el -> e.getKey().equals(el.getKey()))).count())
//                .isEqualTo(a.size());
//    }
}
