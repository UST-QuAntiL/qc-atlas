package org.planqk.atlas.web.utils.modelmapper;

import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.utils.ModelMapperUtils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
