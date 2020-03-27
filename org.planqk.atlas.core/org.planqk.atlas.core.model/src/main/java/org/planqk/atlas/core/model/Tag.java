package org.planqk.atlas.core.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;

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

    @ManyToMany(mappedBy = "tags", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @Setter
    private List<Algorithm> algorithms;

    @ManyToMany(mappedBy = "tags", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @Setter
    private List<Implementation> implementations;

    public void addImplementation(Implementation implementation) {
        implementations.add(implementation);
        implementation.getTags().add(this);
    }

    public void addAlgorithm(Algorithm algorithm) {
        algorithms.add(algorithm);
        algorithm.getTags().add(this);
    }
}
