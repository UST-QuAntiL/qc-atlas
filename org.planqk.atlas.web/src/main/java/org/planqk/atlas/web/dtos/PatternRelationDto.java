package org.planqk.atlas.web.dtos;

import java.net.URI;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode
@Data
public class PatternRelationDto {

    private UUID id;

    @NotNull(message = "Algorithm must not be null!")
    @EqualsAndHashCode.Exclude
    private AlgorithmDto algorithm;

    @NotNull(message = "Pattern-Relations must have a URI!")
    private URI pattern;

    @NotNull(message = "Pattern-Relations must have an type!")
    private PatternRelationTypeDto patternRelationType;

    private String description;
}
