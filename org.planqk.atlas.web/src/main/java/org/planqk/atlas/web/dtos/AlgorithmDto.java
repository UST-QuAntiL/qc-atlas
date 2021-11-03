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

package org.planqk.atlas.web.dtos;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.web.utils.Identifyable;
import org.planqk.atlas.web.utils.ValidationGroups;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer object for Algorithms ({@link org.planqk.atlas.core.model.Algorithm}).
 */
@Data
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "computationModel", visible = true)
@JsonSubTypes({@JsonSubTypes.Type(value = QuantumAlgorithmDto.class, name = "QUANTUM"),
                      @JsonSubTypes.Type(value = ClassicAlgorithmDto.class, name = "CLASSIC"),
                      @JsonSubTypes.Type(value = QuantumAlgorithmDto.class, name = "HYBRID")})
public abstract class AlgorithmDto implements Identifyable {
    @NotNull(groups = {ValidationGroups.IDOnly.class}, message = "An id is required to perform an update")
    @Null(groups = {ValidationGroups.Create.class}, message = "The id must be null for creating an algorithm")
    private UUID id;

    private Date creationDate;

    private Date lastModifiedAt;

    @NotNull(groups = {ValidationGroups.Update.class, ValidationGroups.Create.class},
             message = "Algorithm-Name must not be null!")
    @NotNull(message = "Algorithm-Name must not be null!")
    private String name;

    private String acronym;

    private String intent;

    private String problem;

    private String inputFormat;

    private String algoParameter;

    private String outputFormat;

    private List<SketchDto> sketches;

    private String solution;

    private String assumptions;

    @NotNull(groups = {ValidationGroups.Update.class, ValidationGroups.Create.class},
             message = "Computational-Model must not be null!")
    private ComputationModel computationModel;
}
