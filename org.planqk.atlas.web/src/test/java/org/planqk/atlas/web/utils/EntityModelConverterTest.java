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

import java.util.List;

import javax.validation.constraints.NotNull;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;

import static org.junit.Assert.assertEquals;

public class EntityModelConverterTest {
    @NoArgsConstructor
    @EqualsAndHashCode
    @Data
    private static class SimpleDto {
        @NotNull
        private String notNull;
        private String nullable;
    }

    @Test
    void verifySchema() {
        final var converters = new ModelConverters();
        converters.addConverter(new EntityModelConverter());

        final var normal = converters.resolveAsResolvedSchema(new AnnotatedType().type(SimpleDto.class).resolveAsRef(false));
        assertEquals(List.of("notNull"), normal.schema.getRequired());

        final var instance = new EntityModel<>(new SimpleDto());
        final var wrapped = converters.resolveAsResolvedSchema(new AnnotatedType().type(instance.getClass()).resolveAsRef(false));
        assertEquals(List.of("notNull"), normal.schema.getRequired());
    }
}
