package org.planqk.atlas.web.dtos;

import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import org.planqk.atlas.core.model.LearningMethod;
import org.planqk.atlas.web.utils.Identifyable;
import org.planqk.atlas.web.utils.ValidationGroups;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.server.core.Relation;

/**
 * Data transfer object for LearningMethods ({@link LearningMethod}).
 */
@EqualsAndHashCode
@Data
@Relation(itemRelation = "learningMethod", collectionRelation = "learningMethods")
public class LearningMethodDto implements Identifyable {
    @NotNull(groups = {ValidationGroups.IDOnly.class}, message = "An id is required to perform an update")
    @Null(groups = {ValidationGroups.Create.class}, message = "The id must be null for creating a learning method")
    private UUID id;

    @NotNull(groups = {ValidationGroups.Update.class, ValidationGroups.Create.class},
            message = "The name of a learning method must not be null!")
    private String name;
}
