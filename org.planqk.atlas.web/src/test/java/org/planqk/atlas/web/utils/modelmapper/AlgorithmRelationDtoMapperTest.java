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

import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.AlgorithmRelation;
import org.planqk.atlas.web.dtos.AlgorithmRelationDto;
import org.planqk.atlas.web.utils.ModelMapperUtils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AlgorithmRelationDtoMapperTest {

    @Test
    void mapToDto() {
        var sourceAlgorithm = new Algorithm();
        sourceAlgorithm.setId(UUID.randomUUID());
        var targetAlgorithm = new Algorithm();
        targetAlgorithm.setId(UUID.randomUUID());

        var relation = new AlgorithmRelation();
        relation.setSourceAlgorithm(sourceAlgorithm);
        relation.setTargetAlgorithm(targetAlgorithm);
        relation.setId(UUID.randomUUID());

        var mappedRelation = ModelMapperUtils.convert(relation, AlgorithmRelationDto.class);

        assertThat(mappedRelation.getId()).isEqualTo(relation.getId());
        assertThat(mappedRelation.getSourceAlgorithmId()).isEqualTo(sourceAlgorithm.getId());
        assertThat(mappedRelation.getTargetAlgorithmId()).isEqualTo(targetAlgorithm.getId());
    }

    @Test
    void mapFromDto() {
        var relationDto = new AlgorithmRelationDto();
        relationDto.setId(UUID.randomUUID());
        relationDto.setTargetAlgorithmId(UUID.randomUUID());
        relationDto.setSourceAlgorithmId(UUID.randomUUID());

        var mappedRelation = ModelMapperUtils.convert(relationDto, AlgorithmRelation.class);

        assertThat(mappedRelation.getId()).isEqualTo(relationDto.getId());
        assertThat(mappedRelation.getSourceAlgorithm().getId()).isEqualTo(relationDto.getSourceAlgorithmId());
        assertThat(mappedRelation.getTargetAlgorithm().getId()).isEqualTo(relationDto.getTargetAlgorithmId());
    }
}
