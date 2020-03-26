package org.planqk.atlas.core.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.MapsId;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Tag extends HasId {

    @Getter
    @Setter
    String key;

    @Getter
    @Setter
    String value;

    @ManyToMany
    @Setter
    @MapsId("algorithm_id")
    @EqualsAndHashCode.Include
    private List<Algorithm> algorithms;

    @ManyToMany
    @Setter
    @MapsId("implementation_id")
    @EqualsAndHashCode.Include
    private List<Implementation> implementations;
}
