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

package org.planqk.atlas.web.utils.modelmapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.utils.ModelMapperUtils;

public class ImplementationDtoMapperTest {

    @Test
    void mapToDto() {
        var algorithm = new Algorithm();
        algorithm.setId(UUID.randomUUID());

        var implementation = new Implementation();
        implementation.setImplementedAlgorithm(algorithm);
        implementation.setId(UUID.randomUUID());

        var mappedImplementationDto = ModelMapperUtils.convert(implementation, ImplementationDto.class);

        assertThat(mappedImplementationDto.getId()).isEqualTo(implementation.getId());
        assertThat(mappedImplementationDto.getImplementedAlgorithmId()).isEqualTo(algorithm.getId());
    }

    @Test
    void mapFromDto() {
        var implementationDto = new ImplementationDto();
        implementationDto.setId(UUID.randomUUID());
        implementationDto.setImplementedAlgorithmId(UUID.randomUUID());

        var mappedImplementation = ModelMapperUtils.convert(implementationDto, Implementation.class);

        assertThat(mappedImplementation.getId())
            .isEqualTo(implementationDto.getId());
        assertThat(mappedImplementation.getImplementedAlgorithm().getId())
            .isEqualTo(implementationDto.getImplementedAlgorithmId());
    }
}
