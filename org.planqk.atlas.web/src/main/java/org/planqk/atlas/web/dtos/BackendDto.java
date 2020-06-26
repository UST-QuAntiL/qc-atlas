package org.planqk.atlas.web.dtos;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.planqk.atlas.core.model.BackendProperty;
import org.planqk.atlas.core.model.QuantumComputationModel;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString(callSuper = true)
@Data
@NoArgsConstructor
@JsonSubTypes( {@JsonSubTypes.Type(value = QPUDto.class),
        @JsonSubTypes.Type(value = SimulatorDto.class)})
public class BackendDto {
    private UUID id;

    @NotNull(message = "Backend-Name must not be null!")
    private String name;
    private String vendor;
    private String technology;
    private Set<ComputingResourcePropertyDto> providedQuantumResources = new HashSet<>();
    private QuantumComputationModel quantumComputationModel;
    private Set<BackendProperty> backendProperties;
}
