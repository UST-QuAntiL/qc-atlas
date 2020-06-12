package org.planqk.atlas.core.model;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "backends")
@Data
public class Backend extends HasId {

    private String name;
    private String vendor;
    private String technology;
    private QuantumComputationModel quantumComputationModel;

    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE })
    private Set<BackendProperty> backendProperties = new HashSet<>();
}
