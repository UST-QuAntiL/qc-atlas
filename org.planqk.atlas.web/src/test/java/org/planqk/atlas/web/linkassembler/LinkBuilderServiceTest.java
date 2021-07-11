/*******************************************************************************
 * Copyright (c) 2020-2021 the qc-atlas contributors.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.planqk.atlas.web.annotation.VersionedRequestHandlerMapping;
import org.planqk.atlas.web.utils.ListParametersMethodArgumentResolver;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.RequestMapping;

public class LinkBuilderServiceTest {
    private final VersionedRequestHandlerMapping mappings = new VersionedRequestHandlerMapping();

    private final ListParametersMethodArgumentResolver listParamResolver = new ListParametersMethodArgumentResolver();

    private final LinkBuilderService service = new LinkBuilderService(listParamResolver, mappings);

    @BeforeEach
    public void setupMappings() {
        mappings.populateFromHandler(new Controller());
    }

//    @Test
//    public void invalid() {
//        assertEquals("/", service.linkTo(methodOn(NonController.class).test()).withSelfRel().getHref());
//        assertEquals("/test", service.linkTo(methodOn(Controller.class).nonEndpoint()).withSelfRel().getHref());
//    }
//
//    @Test
//    public void simpleMethodCall() {
//        var link = service.linkTo(methodOn(Controller.class).endpoint()).withSelfRel();
//        assertEquals(IanaLinkRelations.SELF, link.getRel());
//        assertEquals("/test/test", link.getHref());
//    }

//    @Test
//    public void simpleSlash() {
//        var link = service.linkTo(methodOn(Controller.class).endpoint()).slash("something").withSelfRel();
//        assertEquals(IanaLinkRelations.SELF, link.getRel());
//        assertEquals("/test/test/something", link.getHref());
//    }

    static class NonController {
        public HttpEntity<Void> test() {
            return null;
        }
    }

    @RequestMapping("/test")
    static class Controller {
        @RequestMapping("/test")
        public HttpEntity<Void> endpoint() {
            return null;
        }

        public HttpEntity<Void> nonEndpoint() {
            return null;
        }
    }
}
