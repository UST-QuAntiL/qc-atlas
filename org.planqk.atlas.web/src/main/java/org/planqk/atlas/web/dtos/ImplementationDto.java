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

import java.net.URL;
import java.util.UUID;

import javax.validation.constraints.*;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Data transfer object for the model class Implementation
 * ({@link org.planqk.atlas.core.model.Implementation}).
 */
@EqualsAndHashCode
@Data
@NoArgsConstructor
public class ImplementationDto {

    private UUID id;

    @NotNull(message = "Implementation-Name must not be null!")
    private String name;
    @NotNull(message = "Implementation-FileLocation must not be null!")
    private URL link;

    private String inputFormat;
    private String outputFormat;
    private String description;
    private String contributors;
    private String assumptions;
    private String parameter;
    private String dependencies;

    @JsonIgnore
    private AlgorithmDto implementedAlgorithm;

}
