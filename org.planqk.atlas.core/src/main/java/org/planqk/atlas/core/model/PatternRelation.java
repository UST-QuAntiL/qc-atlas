package org.planqk.atlas.core.model;

import java.net.URI;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class PatternRelation extends HasId {

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Algorithm algorithm;

	@Setter
	@Getter
	private URI pattern;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private PatternRelationType patternRelationType;

	@Setter
	@Getter
	private String description;
	
}
