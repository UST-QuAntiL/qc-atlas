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

package org.planqk.quality.api.dtos.entities;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.util.Lists;
import org.planqk.quality.model.Sdk;
import org.springframework.hateoas.RepresentationModel;

/**
 * Data transfer object for multiple {@link Sdk}s.
 */
public class SdkListDto extends RepresentationModel<SdkListDto> {

    private final List<SdkDto> sdkDtos = Lists.newArrayList();

    public List<SdkDto> getSdks() {
        return this.sdkDtos;
    }

    public void add(final SdkDto... sdk) {
        this.sdkDtos.addAll(Arrays.asList(sdk));
    }
}
