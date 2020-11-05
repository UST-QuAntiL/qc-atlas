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

import javax.validation.constraints.NotNull;

import org.planqk.atlas.core.model.ImplementationPackageType;
import org.planqk.atlas.web.utils.ValidationGroups;
import org.springframework.hateoas.server.core.Relation;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "packageType", visible = true)
@JsonSubTypes({@JsonSubTypes.Type(value = DefaultFileImplementationPackageDto.class, name = "DEFAULT_FILE"),
        @JsonSubTypes.Type(value = TOSCAImplementationPackageDto.class, name = "TOSCA"),
        @JsonSubTypes.Type(value = FunctionImplementationPackageDto.class, name = "FUNCTION")})
@Relation(itemRelation = "implementationPackage", collectionRelation = "implementationPackages")
public class ImplementationPackageDto {

    private String name;

    private String description;

    @NotNull(groups = {ValidationGroups.Update.class, ValidationGroups.Create.class},
            message = "PackageType must not be null!")
    private ImplementationPackageType packageType;

}
