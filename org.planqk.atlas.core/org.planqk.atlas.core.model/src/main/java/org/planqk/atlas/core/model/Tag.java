package org.planqk.atlas.core.model;

import javax.persistence.Entity;

@Entity
public class Tag extends HasId {
    String description;
    String name;
}
