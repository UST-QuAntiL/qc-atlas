/*******************************************************************************
 * Copyright (c) 2020 the qc-atlas contributors.
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

import java.util.List;

import org.springframework.hateoas.Affordance;
import org.springframework.hateoas.TemplateVariables;
import org.springframework.hateoas.server.core.TemplateVariableAwareLinkBuilderSupport;
import org.springframework.web.util.UriComponents;

/**
 * Helper class that mimics {@link org.springframework.hateoas.server.mvc.WebMvcLinkBuilder}
 * <p>
 * Needed to retain WebMvcLinkBuilder's flexibility and interface.
 */
public class LinkBuilder extends TemplateVariableAwareLinkBuilderSupport<LinkBuilder> {
    LinkBuilder(UriComponents components) {
        super(components, TemplateVariables.NONE, List.of());
    }

    private LinkBuilder(UriComponents components, TemplateVariables variables, List<Affordance> affordances) {
        super(components, variables, affordances);
    }

    @Override
    protected LinkBuilder getThis() {
        return this;
    }

    @Override
    protected LinkBuilder createNewInstance(UriComponents components, List<Affordance> affordances, TemplateVariables variables) {
        return new LinkBuilder(components, variables, affordances);
    }
}
