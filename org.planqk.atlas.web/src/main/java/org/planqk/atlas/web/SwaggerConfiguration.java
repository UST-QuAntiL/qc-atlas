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
package org.planqk.atlas.web;

import org.planqk.atlas.web.utils.LinkRemoverModelConverter;

import io.swagger.v3.core.converter.ModelConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * This configuration contains the Swagger / SpringDoc configurations we need.
 *
 * This includes custom ModelConverters and type overrides.
 */
@Configuration
public class SwaggerConfiguration {
    @Bean
    @Lazy(false)
    public LinkRemoverModelConverter linkRemoverModelConverter() {
        final var converter = new LinkRemoverModelConverter();
        ModelConverters.getInstance().addConverter(converter);
        return converter;
    }
}
