package org.planqk.atlas.core.model;

import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class ProblemType extends HasId {

	private String name;
	private Long parentProblemType;
	
}
