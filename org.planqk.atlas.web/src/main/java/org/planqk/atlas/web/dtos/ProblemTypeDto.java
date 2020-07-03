package org.planqk.atlas.web.dtos;

import java.util.UUID;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.*;

@EqualsAndHashCode
@Data
@Relation(itemRelation = "problemType", collectionRelation = "problemTypes")
public class ProblemTypeDto {
    private UUID id;

    @NotNull(message = "ProblemType-Name must not be null!")
    private String name;

    private UUID parentProblemType;

}
