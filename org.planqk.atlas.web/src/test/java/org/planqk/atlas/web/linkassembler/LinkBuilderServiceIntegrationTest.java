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
package org.planqk.atlas.web.linkassembler;

import org.planqk.atlas.web.SpringOverrides;
import org.planqk.atlas.web.controller.RootController;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.IanaLinkRelations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@WebMvcTest( {RootController.class, DummyController.class})
@EnableLinkAssemblers
@Import(SpringOverrides.class)
public class LinkBuilderServiceIntegrationTest {
    @Autowired
    private LinkBuilderService service;

    @Test
    public void resolvesAsUnversioned() {
        var link = service.linkTo(methodOn(RootController.class).root()).withSelfRel();
        assertEquals(IanaLinkRelations.SELF, link.getRel());
        assertEquals("/", link.getHref());
    }

    @Test
    public void resolvesAsVersioned() {
        var link = service.linkTo(methodOn(DummyController.class).test()).withSelfRel();
        assertEquals(IanaLinkRelations.SELF, link.getRel());
        assertEquals("/controller/v1/test", link.getHref());
    }
}
