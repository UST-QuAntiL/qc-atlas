package org.planqk.atlas.web.dtos;

import java.util.UUID;

import java.net.URI;

import javax.validation.constraints.NotNull;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.PatternRelationType;
import io.swagger.v3.oas.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.WRITE_ONLY;

@NoArgsConstructor
@EqualsAndHashCode
@Data
public class PatternRelationDto {

    private UUID id;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(accessMode = WRITE_ONLY)
    @NotNull(message = "Pattern-Relations must have an algorithm!")
    private Algorithm algorithm;

    @NotNull(message = "Pattern-Relations must have a URI!")
    private URI pattern;

    @NotNull(message = "Pattern-Relations must have an type!")
    private PatternRelationType patternRelationType;

    private String description;

}
