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

package org.planqk.atlas.web.dtos;

import org.planqk.atlas.core.model.ImplementationPackageType;
import org.springframework.hateoas.server.core.Relation;

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
@JsonTypeName("DEFAULT_FILE")
@Relation(itemRelation = "implementationPackage", collectionRelation = "implementationPackages")
public class DefaultFileImplementationPackageDto extends ImplementationPackageDto {


    @Override
    @Schema(type = "string", allowableValues = {"DEFAULT_FILE"})
    public ImplementationPackageType getPackageType() {
        return super.getPackageType();
    }
}
