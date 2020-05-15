package org.planqk.atlas.core.model;

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
public class AlgorithmRelation extends HasId {
	
	@ManyToOne(cascade = CascadeType.ALL)
	private Algorithm sourceAlgorithm;
	
	@ManyToOne(cascade = CascadeType.ALL)
	private Algorithm targetAlgorithm;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinTable(
			name = "type_of_algorithm_relation",
			joinColumns = @JoinColumn(name = "algorithmRelation_id"),
			inverseJoinColumns = @JoinColumn(name = "algoRelationType_id"))
	private AlgoRelationType algoRelationType;
	
	@Setter
	@Getter
	private String description;
	
}
