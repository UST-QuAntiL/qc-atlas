/********************************************************************************
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

package org.planqk.atlas.web.dtos;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;
import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.WRITE_ONLY;

/**
 * Data transfer object for Algorithms ({@link org.planqk.atlas.core.model.Algorithm}).
 */
@NoArgsConstructor
@Data
public class AlgorithmRelationDto {

    private UUID id;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(accessMode = WRITE_ONLY)
    @EqualsAndHashCode.Exclude
    private UUID sourceAlgorithmId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(accessMode = WRITE_ONLY)
    @EqualsAndHashCode.Exclude
    private UUID targetAlgorithmId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(accessMode = READ_ONLY)
    @EqualsAndHashCode.Exclude
    private AlgorithmDto sourceAlgorithm;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(accessMode = READ_ONLY)
    @EqualsAndHashCode.Exclude
    private AlgorithmDto targetAlgorithm;

    @NotNull(message = "AlgorithmRelationType must not be null!")
    private AlgoRelationTypeDto algoRelationType;

    private String description;
}
