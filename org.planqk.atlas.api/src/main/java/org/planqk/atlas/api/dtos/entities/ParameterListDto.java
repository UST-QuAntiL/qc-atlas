/*
 *  /*******************************************************************************
 *  * Copyright (c) 2020 University of Stuttgart
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  * in compliance with the License. You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License
 *  * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *  * or implied. See the License for the specific language governing permissions and limitations under
 *  * the License.
 *  ******************************************************************************
 */

package org.planqk.atlas.api.dtos.entities;

import java.util.List;

import org.planqk.atlas.core.model.Parameter;

import org.assertj.core.util.Lists;
import org.springframework.hateoas.RepresentationModel;

/**
 * Data transfer object for multiple {@link Parameter}s
 */
public class ParameterListDto extends RepresentationModel<ParameterListDto> {

    private List<ParameterDto> parameterDtos = Lists.newArrayList();

    public List<ParameterDto> getParameters() {
        return this.parameterDtos;
    }

    public void add(final List<ParameterDto> parameters) {
        this.parameterDtos.addAll(parameters);
    }
}
