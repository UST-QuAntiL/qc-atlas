package org.planqk.atlas.core.model;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class BackendPropertyType extends HasId {

    @NotNull(message = "BackendPropertyType-name must not be null!")
    private String name;

    private String description;
}
