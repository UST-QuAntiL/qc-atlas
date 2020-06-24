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

import com.fasterxml.jackson.databind.JavaType;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

/**
 * Removes `_links` from CollectionModel, PagedModel, ...
 */
@RequiredArgsConstructor
public class LinkRemoverModelConverter implements ModelConverter {
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
            if (RepresentationModel.class.isAssignableFrom(cls)) {
                if (annotatedType.isResolveAsRef()) {
                    // Call resolve() with resolveAsRef = false, so this method here is called again
                    // and we get to edit the type's real schema.
                    context.resolve(annotatedType.resolveAsRef(false));
                    annotatedType.resolveAsRef(true);
                }
                final var schema = chain.next().resolve(annotatedType, context, chain);
                if (schema == null)
                    return null;
                if (schema.getProperties() != null) {
                    schema.getProperties().remove("_links");
                }
                return schema;
            }
        }
        if (chain.hasNext()) {
            return chain.next().resolve(annotatedType, context, chain);
        } else {
            return null;
        }
    }
}
