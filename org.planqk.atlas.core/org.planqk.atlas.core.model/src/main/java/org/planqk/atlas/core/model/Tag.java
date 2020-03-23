package org.planqk.atlas.core.model;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Entity
public class Tag extends HasId {

    @Getter
    @Setter
    String description;

    @Getter
    @Setter
    String name;
}
