package org.planqk.atlas.web.dtos;

import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import org.planqk.atlas.core.model.QuantumComputationModel;
import org.planqk.atlas.web.utils.Identifyable;
import org.planqk.atlas.web.utils.ValidationGroups;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.hateoas.server.core.Relation;

@ToString(callSuper = true)
@Data
@NoArgsConstructor
@JsonSubTypes( {@JsonSubTypes.Type(value = QPUDto.class),
        @JsonSubTypes.Type(value = SimulatorDto.class)})
@Relation(itemRelation = "computeResource", collectionRelation = "computeResources")
public class ComputeResourceDto implements Identifyable {

    @NotNull(groups = {ValidationGroups.Update.class}, message = "An id is required to perform an update")
    @Null(groups = {ValidationGroups.Create.class}, message = "The id must be null for creating a compute resource")
    private UUID id;

    @NotNull(groups = {ValidationGroups.Update.class, ValidationGroups.Create.class},
            message = "Compute Resource Name must not be null!")
    private String name;

    private String vendor;

    private String technology;

    private QuantumComputationModel quantumComputationModel;
}
