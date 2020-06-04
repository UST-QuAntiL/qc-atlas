package org.planqk.atlas.web.dtos;

import java.util.UUID;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;

@EqualsAndHashCode
@Data
public class ProblemTypeDto {
    private UUID id;

    @NotNull(message = "ProblemType-Name must not be null!")
    private String name;

    private UUID parentProblemType;

}
