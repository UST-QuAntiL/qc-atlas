package org.planqk.atlas.web.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.planqk.atlas.core.model.QuantumComputationModel;

@ToString(callSuper = true)
@Data
@NoArgsConstructor
public class BackendDto {
    private String name;
    private String vendor;
    private String technology;
    private QuantumComputationModel quantumComputationModel;
}
