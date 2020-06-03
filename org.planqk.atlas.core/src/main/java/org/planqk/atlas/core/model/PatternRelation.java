package org.planqk.atlas.core.model;

import java.net.URI;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class PatternRelation extends HasId {

	@ManyToOne
	private Algorithm algorithm;

	private URI pattern;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private PatternRelationType patternRelationType;

	private String description;
	
}
