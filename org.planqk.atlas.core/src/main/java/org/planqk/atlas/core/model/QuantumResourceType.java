package org.planqk.atlas.core.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class QuantumResourceType extends HasId {

    @Column
    @Setter
    @Getter
    private String name;

    @Lob
    @Column
    @Setter
    @Getter
    private String description;

    @Column
    @Setter
    @Getter
    private QuantumResourceDataType dataType;


}
