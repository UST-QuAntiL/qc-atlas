package org.planqk.atlas.core.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class PatternRelation extends HasId {

	@ManyToOne
	private Algorithm algorithm;
	
}
