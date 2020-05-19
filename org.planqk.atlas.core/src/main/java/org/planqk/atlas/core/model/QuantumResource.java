package org.planqk.atlas.core.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class QuantumResource extends HasId {

    @Column
    @ManyToOne(optional = false)
    private QuantumResourceType type;

    @Lob
    @Column
    //TODO Change to proper datatype
    private String value;
}
