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

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.web.dtos.PatternRelationDto;
import org.planqk.atlas.web.utils.ModelMapperUtils;

public class PatternRelationDtoMapperTest {

    @Test
    void mapToDto() {
        Algorithm algorithm = new ClassicAlgorithm();
        algorithm.setId(UUID.randomUUID());

        var patternRelation = new PatternRelation();
        patternRelation.setAlgorithm(algorithm);
        patternRelation.setId(UUID.randomUUID());

        var mappedRelation = ModelMapperUtils.convert(patternRelation, PatternRelationDto.class);

        assertThat(mappedRelation.getId()).isEqualTo(patternRelation.getId());
        assertThat(mappedRelation.getAlgorithmId()).isEqualTo(algorithm.getId());
    }

    @Test
    void mapFromDto() {
        var patternRelationDto = new PatternRelationDto();
        patternRelationDto.setId(UUID.randomUUID());
        patternRelationDto.setAlgorithmId(UUID.randomUUID());

        var mappedPatternRelation = ModelMapperUtils.convert(patternRelationDto, PatternRelationDto.class);

        assertThat(mappedPatternRelation.getId())
                .isEqualTo(patternRelationDto.getId());
        assertThat(mappedPatternRelation.getAlgorithmId())
                .isEqualTo(patternRelationDto.getAlgorithmId());
    }
}
