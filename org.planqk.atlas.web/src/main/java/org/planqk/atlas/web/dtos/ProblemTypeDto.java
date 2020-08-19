package org.planqk.atlas.web.dtos;

import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import org.planqk.atlas.web.utils.ValidationGroups;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.server.core.Relation;

@EqualsAndHashCode
@Data
@Relation(itemRelation = "problemType", collectionRelation = "problemTypes")
public class ProblemTypeDto {
    @NotNull(groups = {ValidationGroups.Update.class}, message = "An id is required to perform an update")
    @Null(groups = {ValidationGroups.Create.class}, message = "The id must be null for creating a problem type")
    private UUID id;

    @NotNull(message = "ProblemType-Name must not be null!")
    private String name;

    private UUID parentProblemType;

}
