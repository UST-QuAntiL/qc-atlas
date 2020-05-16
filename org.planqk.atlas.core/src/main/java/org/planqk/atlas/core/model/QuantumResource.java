package org.planqk.atlas.core.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Type;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class QuantumResource extends HasId {

	@OneToOne(fetch = FetchType.LAZY)
	QuantumResourceType quantumResourceType;
	
	@ManyToOne
	QuantumAlgorithm algorithm;
	
	@Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
	Object value;
}
