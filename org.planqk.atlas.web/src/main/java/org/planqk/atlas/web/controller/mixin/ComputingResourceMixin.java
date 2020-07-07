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
package org.planqk.atlas.web.controller.mixin;

import java.util.Objects;

import org.planqk.atlas.core.model.ComputingResourceProperty;
import org.planqk.atlas.core.services.ComputingResourcePropertyService;
import org.planqk.atlas.web.dtos.ComputingResourcePropertyDto;
import org.planqk.atlas.web.exceptions.InvalidParameterException;
import org.planqk.atlas.web.utils.ModelMapperUtils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ComputingResourceMixin {
    private final ComputingResourcePropertyService computingResourcePropertyService;

    public ComputingResourceProperty fromDto(ComputingResourcePropertyDto resourceDto) {
        if (Objects.isNull(resourceDto.getType().getId())) {
            throw new InvalidParameterException("empty type ID");
        }
        var type = computingResourcePropertyService.findComputingResourcePropertyTypeById(resourceDto.getType().getId());
        var resource = ModelMapperUtils.convert(resourceDto, ComputingResourceProperty.class);
        resource.setComputingResourcePropertyType(type);
        return resource;
    }
}
