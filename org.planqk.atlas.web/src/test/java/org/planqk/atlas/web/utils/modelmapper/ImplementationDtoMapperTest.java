/*******************************************************************************
 * Copyright (c) 2020-2021 the qc-atlas contributors.
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.model.ClassicImplementation;
import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.core.services.AlgorithmServiceImpl;
import org.planqk.atlas.core.services.SketchService;
import org.planqk.atlas.web.controller.TagController;
import org.planqk.atlas.web.dtos.ClassicImplementationDto;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.linkassembler.EnableLinkAssemblers;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(ModelMapperUtils.class)
public class ImplementationDtoMapperTest {

    @MockBean
    private AlgorithmService algorithmService;

    @Autowired
    MockMvc mockMvc;

    @Test
    void mapToDto() {
        Algorithm algorithm = new ClassicAlgorithm();
        algorithm.setId(UUID.randomUUID());
        algorithm.setComputationModel(ComputationModel.CLASSIC);

        Implementation implementation = new ClassicImplementation();
        implementation.setImplementedAlgorithm(algorithm);
        implementation.setId(UUID.randomUUID());

        when(algorithmService.findById(any())).thenReturn(algorithm);


        var mappedImplementationDto = ModelMapperUtils.convert(implementation, ClassicImplementationDto.class);

        assertThat(mappedImplementationDto.getId()).isEqualTo(implementation.getId());
        assertThat(mappedImplementationDto.getImplementedAlgorithmId()).isEqualTo(algorithm.getId());
    }

    @Test
    void mapFromDto() {
        Algorithm algorithm = new ClassicAlgorithm();
        UUID algoId = UUID.randomUUID();
        algorithm.setId(algoId);
        algorithm.setComputationModel(ComputationModel.CLASSIC);
        when(algorithmService.findById(any())).thenReturn(algorithm);
        ImplementationDto implementationDto = new ClassicImplementationDto();
        implementationDto.setId(UUID.randomUUID());
        implementationDto.setImplementedAlgorithmId(UUID.randomUUID());
        implementationDto.setName("test");
        var mappedImplementation = ModelMapperUtils.convert(implementationDto, Implementation.class);
        assertThat(mappedImplementation.getId())
                .isEqualTo(implementationDto.getId());
        assertThat(mappedImplementation.getImplementedAlgorithm().getId())
                .isEqualTo(implementationDto.getImplementedAlgorithmId());
    }
}
