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
package org.planqk.atlas.web.utils;

import java.util.Iterator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.media.Schema;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Links;

/**
 * Custom converter for EntityModel classes.
 * <p>
 * Spring HATEOAS' EntityModel class is special because it uses Jackson's @JsonWrapped to "copy" the actual DTO's
 * properties into the EntityModel instance. Unfortunately, information on whether properties are required or not is
 * lost in the process. This wrapper aims to fix that by copying the DTO's schema and then manually adding the
 * EntityModel-specific fields (for now, just _links).
 */
public class EntityModelConverter implements ModelConverter {
    @Override
    public Schema resolve(AnnotatedType annotatedType, ModelConverterContext context, Iterator<ModelConverter> chain) {
        final JavaType type;
        if (annotatedType.getType() instanceof JavaType) {
            type = (JavaType) annotatedType.getType();
        } else {
            type = Json.mapper().constructType(annotatedType.getType());
        }
        if (type != null) {
            final var cls = type.getRawClass();
            if (EntityModel.class.isAssignableFrom(cls)) {
                return resolveEntityModel(type, context);
            }
        }
        if (chain.hasNext()) {
            return chain.next().resolve(annotatedType, context, chain);
        } else {
            return null;
        }
    }

    private Schema resolveEntityModel(JavaType type, ModelConverterContext context) {
        final var entityType = type.getBindings().getBoundType(0);
        final Schema resolved = context.resolve(new AnnotatedType().type(entityType).resolveAsRef(false));
        if (resolved == null)
            throw new RuntimeException(String.format("Cannot resolve %s", entityType.getTypeName()));

        try {
            final Schema wrapper = clone(resolved);
            wrapper.name(String.format("EntityModel%s", wrapper.getName()));
            wrapper.addProperties("_links", context.resolve(new AnnotatedType()
                    .type(Links.class)
                    .resolveAsRef(true)));
            return wrapper;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Schema clone(Schema property) throws JsonProcessingException {
        if (property == null)
            return null;

        String cloneName = property.getName();
        property = Json.mapper().readValue(Json.pretty(property), Schema.class);
        property.setName(cloneName);
        return property;
    }
}
