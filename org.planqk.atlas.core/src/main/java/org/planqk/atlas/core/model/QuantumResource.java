package org.planqk.atlas.core.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@TypeDef(
        name = "jsonb",
        typeClass = JsonBinaryType.class
)
public class QuantumResource extends HasId {

	@OneToOne(fetch = FetchType.LAZY)
	private QuantumResourceType quantumResourceType;
	
	@ManyToOne
	private QuantumAlgorithm algorithm;
	
	@Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
	private Object value;
}
