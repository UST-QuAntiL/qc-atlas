package org.planqk.atlas.web.dtos;

import java.util.UUID;

import java.net.URI;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode
@Data
public class PatternRelationDto {

    private UUID id;

//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//    @Schema(accessMode = WRITE_ONLY)
    @NotNull(message = "Pattern-Relations must have an algorithm!")
    private AlgorithmDto algorithm;

    @NotNull(message = "Pattern-Relations must have a URI!")
    private URI pattern;

//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//    @Schema(accessMode = WRITE_ONLY)
    @NotNull(message = "Pattern-Relations must have an type!")
    private PatternRelationTypeDto patternRelationType;

    private String description;

}
