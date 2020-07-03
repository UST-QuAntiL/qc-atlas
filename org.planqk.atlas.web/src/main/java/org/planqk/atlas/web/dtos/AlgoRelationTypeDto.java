package org.planqk.atlas.web.dtos;

import java.util.UUID;

import javax.validation.constraints.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.server.core.Relation;

@Data
@NoArgsConstructor
@Relation(itemRelation = "algoRelationType", collectionRelation = "algoRelationTypes")
public class AlgoRelationTypeDto {

    private UUID id;

    @NotNull(message = "RelationType-Name must not be null!")
    private String name;

}
