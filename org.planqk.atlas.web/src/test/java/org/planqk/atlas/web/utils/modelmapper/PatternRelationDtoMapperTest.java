package org.planqk.atlas.web.utils.modelmapper;

import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.web.dtos.PatternRelationDto;
import org.planqk.atlas.web.utils.ModelMapperUtils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PatternRelationDtoMapperTest {

    @Test
    void mapToDto() {
        var algorithm = new Algorithm();
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
