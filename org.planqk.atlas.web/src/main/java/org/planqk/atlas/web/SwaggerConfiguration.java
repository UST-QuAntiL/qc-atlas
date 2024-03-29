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

package org.planqk.atlas.web;

import java.util.Map;

import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.ClassicAlgorithmDto;
import org.planqk.atlas.web.dtos.ClassicImplementationDto;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.dtos.QuantumAlgorithmDto;
import org.planqk.atlas.web.dtos.QuantumImplementationDto;
import org.planqk.atlas.web.utils.OverrideModelConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * This configuration contains the Swagger / SpringDoc configurations we need.
 * <p>
 * This includes custom ModelConverters and type overrides.
 */
@Configuration
public class SwaggerConfiguration {

    /**
     * ModelConverter that allows overriding {@link io.swagger.v3.oas.models.media.Schema} objects for Java classes.
     * Required for the different models (Quantum, Hybrid, or Classic) of AlgorithmDto and ImplementationDto,
     * see private classes AlgorithmSchema and ImplementationSchema below.
     */
    @Bean
    @Lazy(false)
    public OverrideModelConverter overrideModelConverter() {
        final var converter = new OverrideModelConverter(Map.of(
            AlgorithmDto.class, AlgorithmSchema.class,
            ImplementationDto.class, ImplementationSchema.class
        ));
        ModelConverters.getInstance().addConverter(converter);
        return converter;
    }

    // The private classes below provide custom schemas for certain types used in our public API.
    // Setting these annotations on the correct types is not always possible, because we could end up with
    // reference cycles, for example:
    //
    // AlgorithmDto -- (via oneOf) --> ClassicAlgorithmDto -- (extends) --> AlgorithmDto

    @Schema(
        name = "AlgorithmDto",
        description = "Either a quantum, hybrid or a classic algorithm",
        discriminatorProperty = "computationModel",
        oneOf = {ClassicAlgorithmDto.class, QuantumAlgorithmDto.class},
        discriminatorMapping = {
            @DiscriminatorMapping(value = "CLASSIC", schema = ClassicAlgorithmDto.class),
            @DiscriminatorMapping(value = "QUANTUM", schema = QuantumAlgorithmDto.class),
            @DiscriminatorMapping(value = "HYBRID", schema = QuantumAlgorithmDto.class)
        }
    )
    private static class AlgorithmSchema {
    }

    @Schema(
        name = "ImplementationDto",
        description = "Either a quantum, hybrid or a classic implementation",
        oneOf = {ClassicImplementationDto.class, QuantumImplementationDto.class},
        discriminatorMapping = {
            @DiscriminatorMapping(value = "CLASSIC", schema = ClassicImplementationDto.class),
            @DiscriminatorMapping(value = "QUANTUM", schema = QuantumImplementationDto.class),
            @DiscriminatorMapping(value = "HYBRID", schema = QuantumImplementationDto.class),
        }
    )
    private static class ImplementationSchema {
    }
}
