package org.planqk.atlas.core.model;

import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class BackendPropertyType extends HasId {

    @NonNull
    private String name;
    private String description;
}
