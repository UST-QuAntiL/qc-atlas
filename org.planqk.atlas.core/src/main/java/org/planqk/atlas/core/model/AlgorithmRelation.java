package org.planqk.atlas.core.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class AlgorithmRelation extends HasId {

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "sourceAlgorithm_id", referencedColumnName = "id")
	private Algorithm sourceAlgorithm;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "targetAlgorithm_id", referencedColumnName = "id")
	private Algorithm targetAlgorithm;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "algoRelationType_id", referencedColumnName = "id")
	private AlgoRelationType algoRelationType;

	private String description;

}
