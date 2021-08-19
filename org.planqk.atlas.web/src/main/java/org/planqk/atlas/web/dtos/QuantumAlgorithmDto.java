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

import javax.validation.constraints.NotNull;

import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.QuantumComputationModel;

import com.fasterxml.jackson.annotation.JsonTypeName;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Data transfer object for QuantumAlgorithm ({@link org.planqk.atlas.core.model.QuantumAlgorithm}).
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@JsonTypeName("QUANTUM")
public class QuantumAlgorithmDto extends AlgorithmDto {

    private boolean nisqReady;

    @NotNull(message = "QuantumComputationModel must not be null!")
    private QuantumComputationModel quantumComputationModel;

    private String speedUp;

    @Override
    @Schema(type = "string", allowableValues = {"QUANTUM", "HYBRID"})
    public ComputationModel getComputationModel() {
        return super.getComputationModel();
    }
}
