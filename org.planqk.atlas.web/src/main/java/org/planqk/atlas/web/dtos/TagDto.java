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

import javax.validation.constraints.NotNull;

import org.planqk.atlas.web.utils.ValidationGroups;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.server.core.Relation;

@EqualsAndHashCode
@Data
@Relation(itemRelation = "tag", collectionRelation = "tags")
public class TagDto {

    private String category;

    @NotNull(groups = {ValidationGroups.IDOnly.class, ValidationGroups.Create.class}, message = "Tag value must not be null!")
    private String value;
}
