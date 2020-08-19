package org.planqk.atlas.web.dtos;

import java.net.URI;
import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import org.planqk.atlas.web.utils.Identifyable;
import org.planqk.atlas.web.utils.RequiresID;
import org.planqk.atlas.web.utils.ValidationGroups;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.server.core.Relation;

@NoArgsConstructor
@EqualsAndHashCode
@Data
@Relation(itemRelation = "patternRelation", collectionRelation = "patternRelations")
public class PatternRelationDto implements Identifyable {

    @NotNull(groups = {ValidationGroups.Update.class}, message = "An id is required to perform an update")
    @Null(groups = {ValidationGroups.Create.class}, message = "The id must be null for creating a pattern relation")
    private UUID id;

    @NotNull(groups = {ValidationGroups.Update.class, ValidationGroups.Create.class},
            message = "Algorithm must not be null!")
    @EqualsAndHashCode.Exclude
    private UUID algorithmId;

    @NotNull(groups = {ValidationGroups.Update.class, ValidationGroups.Create.class},
            message = "Pattern-Relations must have a URI!")
    private URI pattern;

    @NotNull(groups = {ValidationGroups.Update.class, ValidationGroups.Create.class},
            message = "Pattern-Relations must have a type!")
    @RequiresID(groups = {ValidationGroups.Update.class, ValidationGroups.Create.class},
            message = "Pattern-Relations must have a type with an ID!")
    private PatternRelationTypeDto patternRelationType;

    private String description;
}
