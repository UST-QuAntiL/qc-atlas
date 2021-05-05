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

import java.util.UUID;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import org.planqk.atlas.core.model.ComputeResourcePropertyDataType;
import org.planqk.atlas.web.utils.Identifyable;
import org.planqk.atlas.web.utils.ValidationGroups;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.hateoas.server.core.Relation;

/**
 * Data transfer object for ComputeResourcePropertyType ({@link org.planqk.atlas.core.model.ComputeResourcePropertyType}).
 */
@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@Relation(itemRelation = "computeResourcePropertyType", collectionRelation = "computeResourcePropertyTypes")
public class ComputeResourcePropertyTypeDto implements Identifyable {

    @NotNull(groups = {ValidationGroups.IDOnly.class}, message = "An id is required to perform an update")
    @Null(groups = {ValidationGroups.Create.class}, message = "The id must be null for creating a compute resource property type")
    private UUID id;

    @NotEmpty(groups = {ValidationGroups.Update.class, ValidationGroups.Create.class})
    private String name;

    @NotNull(groups = {ValidationGroups.Update.class, ValidationGroups.Create.class})
    private ComputeResourcePropertyDataType datatype;

    private String description;
}
