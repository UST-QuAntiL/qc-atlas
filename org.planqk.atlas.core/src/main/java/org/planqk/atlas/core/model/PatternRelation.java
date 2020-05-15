package org.planqk.atlas.core.model;

import java.net.URI;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
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

	@ManyToOne(cascade = CascadeType.ALL)
	private Algorithm algorithm;

	@Setter
	@Getter
	private URI pattern;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinTable(
			name = "type_of_pattern_relation",
			joinColumns = @JoinColumn(name = "patternRelation_id"),
			inverseJoinColumns = @JoinColumn(name = "patternRelationType_id"))
	private PatternRelationType patternRelationType;

	@Setter
	@Getter
	private String description;
	
}
