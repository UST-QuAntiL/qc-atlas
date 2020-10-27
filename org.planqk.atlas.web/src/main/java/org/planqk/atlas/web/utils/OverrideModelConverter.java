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

package org.planqk.atlas.web.utils;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.core.type.ResolvedType;
import com.fasterxml.jackson.databind.JavaType;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.media.Schema;
import lombok.RequiredArgsConstructor;

/**
 * ModelConverter that allows overriding {@link Schema} objects for Java classes.
 */
@RequiredArgsConstructor
public class OverrideModelConverter implements ModelConverter {
    private final Map<Type, Type> contentOverrides;

    private static Class<?> classFromType(Type type) {
        if (type instanceof Class<?>)
            return (Class<?>) type;
        if (type instanceof ResolvedType)
            return ((ResolvedType) type).getRawClass();
        return null;
    }

    @Override
    public Schema resolve(AnnotatedType annotatedType, ModelConverterContext context, Iterator<ModelConverter> chain) {
        final JavaType type;
        if (annotatedType.getType() instanceof JavaType) {
            type = (JavaType) annotatedType.getType();
        } else {
            type = Json.mapper().constructType(annotatedType.getType());
        }
        if (type != null) {
            annotatedType.type(applyOverrides(type));
        }
        if (chain.hasNext()) {
            return chain.next().resolve(annotatedType, context, chain);
        } else {
            return null;
        }
    }

    private Type applyOverrides(Type type) {
        final var clazz = classFromType(type);
        if (clazz != null)
            return contentOverrides.getOrDefault(clazz, type);
        return type;
    }
}
