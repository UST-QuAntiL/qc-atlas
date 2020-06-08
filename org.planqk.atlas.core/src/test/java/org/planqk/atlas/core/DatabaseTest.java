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

package org.planqk.atlas.core;

import org.planqk.atlas.core.model.Tag;
import org.planqk.atlas.core.repository.TagRepository;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DatabaseTest extends AtlasDatabaseTestBase {

    @Autowired
    private TagRepository repository;

    @Test
    void modelLoads() {
        // This test is intended to ensure the Database test environment works properly
        // It therefore uses tags since they are fairly simple, ignoring their
        // attachments
        // In case the tag class gets a major rework and this test no longer works, just
        // comment it out!
        var inputTag = new Tag();
        inputTag.setKey("Test");
        inputTag.setValue("test-value");
        var t = repository.save(inputTag);
        assertNotNull(t.getId());
        System.out.println(t.getId());

        var outputTag = repository.findById(t.getId());
        assertTrue(outputTag.isPresent());
        var ot = outputTag.orElseThrow();
        assertEquals(t.getId(), ot.getId());
        assertEquals(t.getKey(), ot.getKey());
        assertEquals(t.getValue(), ot.getValue());
    }
}
