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

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.planqk.atlas.core.model.ApplicationArea;
import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.Sketch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

/**
 * Data transfer object for Algorithms
 * ({@link org.planqk.atlas.core.model.Algorithm}).
 */
@Data
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "computationModel", visible = true)
@JsonSubTypes({ @JsonSubTypes.Type(value = QuantumAlgorithmDto.class, name = "QUANTUM"),
        @JsonSubTypes.Type(value = ClassicAlgorithmDto.class, name = "CLASSIC"),
        @JsonSubTypes.Type(value = QuantumAlgorithmDto.class, name = "HYBRID") })
@Schema(oneOf = {QuantumAlgorithmDto.class, ClassicAlgorithmDto.class}, description = "either a quantum or a classic algorithm", title = "quantum/classic algorithm")
public class AlgorithmDto {
    private UUID id;

    @NotNull(message = "Algorithm-Name must not be null!")
    private String name;

    private String acronym;

    private String intent;

    private String problem;

    private String inputFormat;

    private String algoParameter;

    private String outputFormat;

    private Sketch sketch;

    private String solution;

    private String assumptions;

    @NotNull(message = "Computational-Model must not be null!")
    private ComputationModel computationModel;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(accessMode = READ_ONLY)
    private Set<ProblemTypeDto> problemTypes = new HashSet<>();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(accessMode = READ_ONLY)
    private Set<ApplicationArea> applicationAreas = new HashSet<>();

}
