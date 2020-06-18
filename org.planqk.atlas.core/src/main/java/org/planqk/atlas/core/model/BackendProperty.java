package org.planqk.atlas.core.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class BackendProperty extends HasId {

    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE })
    @NotNull(message = "BackendPropertyType must not be null!")
    private BackendPropertyType type;

    /**
     * TODO: change this to a proper type in current data model
     */
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Object value;
}
