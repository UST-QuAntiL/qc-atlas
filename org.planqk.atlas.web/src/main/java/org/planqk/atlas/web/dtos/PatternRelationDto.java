package org.planqk.atlas.web.dtos;

import java.net.URI;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;
import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.WRITE_ONLY;

@NoArgsConstructor
@EqualsAndHashCode
@Data
public class PatternRelationDto {

    private UUID id;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(accessMode = WRITE_ONLY)
    @NotNull(message = "ID of the Algorithm must not be null!")
    @EqualsAndHashCode.Exclude
    private UUID algorithmId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(accessMode = READ_ONLY)
    private AlgorithmDto algorithm;

    @NotNull(message = "Pattern-Relations must have a URI!")
    private URI pattern;

    @NotNull(message = "Pattern-Relations must have an type!")
    private PatternRelationTypeDto patternRelationType;

    private String description;
}
