package org.planqk.atlas.web.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.hateoas.server.core.Relation;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@Relation(itemRelation = "simulator", collectionRelation = "simulators")
public class SimulatorDto extends BackendDto {

    private boolean localExecution;

    private String licence;

}
