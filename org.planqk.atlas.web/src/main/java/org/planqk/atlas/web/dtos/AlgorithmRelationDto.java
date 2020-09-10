/*******************************************************************************
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
import javax.validation.constraints.Null;

import org.planqk.atlas.web.utils.Identifyable;
import org.planqk.atlas.web.utils.RequiresID;
import org.planqk.atlas.web.utils.ValidationGroups;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.server.core.Relation;

/**
 * Data transfer object for Algorithms ({@link org.planqk.atlas.core.model.Algorithm}).
 */
@NoArgsConstructor
@Data
@Relation(itemRelation = "algorithmRelation", collectionRelation = "algorithmRelations")
public class AlgorithmRelationDto implements Identifyable {

    @NotNull(groups = {ValidationGroups.IDOnly.class}, message = "An id is required to perform an update")
    @Null(groups = {ValidationGroups.Create.class}, message = "The id must be null for creating an algorithm relation")
    private UUID id;

    @NotNull(groups = {ValidationGroups.Update.class, ValidationGroups.Create.class},
            message = "Source Algorithm id must not be null!")
    private UUID sourceAlgorithmId;

    @NotNull(groups = {ValidationGroups.Update.class, ValidationGroups.Create.class},
            message = "Target Algorithm id must not be null!")
    private UUID targetAlgorithmId;

    @JsonProperty("algoRelationType")
    @RequiresID(groups = {ValidationGroups.Update.class, ValidationGroups.Create.class},
            message = "AlgorithmRelationType must have a type with an ID!")
    @NotNull(groups = {ValidationGroups.Update.class, ValidationGroups.Create.class},
            message = "AlgorithmRelationType must not be null!")
    private AlgorithmRelationTypeDto algorithmRelationType;

    private String description;
}
