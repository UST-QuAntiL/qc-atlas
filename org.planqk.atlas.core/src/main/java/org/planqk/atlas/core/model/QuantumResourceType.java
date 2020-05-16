package org.planqk.atlas.core.model;

import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class QuantumResourceType extends HasId {

	private String name;
	private Datatype datatype;
	private String description;
	
}
