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
