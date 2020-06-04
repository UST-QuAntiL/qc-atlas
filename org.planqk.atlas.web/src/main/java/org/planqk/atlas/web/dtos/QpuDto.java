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

import org.planqk.atlas.core.model.Qpu;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

import javax.validation.constraints.*;

/**
 * Data transfer object for the model class {@link Qpu}.
 */
@EqualsAndHashCode
@Data
@NoArgsConstructor
public class QpuDto {

    private UUID id;

    @NotNull(message = "Qpu-Name must not be null!")
    private String name;

    private int numberOfQubits;

    private float t1;

    private float maxGateTime;
    
    @JsonIgnore
    private ProviderDto provider;
    
}
