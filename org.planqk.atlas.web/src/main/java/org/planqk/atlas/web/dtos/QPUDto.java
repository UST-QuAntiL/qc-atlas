package org.planqk.atlas.web.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.hateoas.server.core.Relation;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@Relation(itemRelation = "qpu", collectionRelation = "qpus")
public class QPUDto extends BackendDto {
}
