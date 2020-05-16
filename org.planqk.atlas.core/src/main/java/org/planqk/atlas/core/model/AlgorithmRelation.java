package org.planqk.atlas.core.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class AlgorithmRelation extends HasId {
	
	@ManyToOne(targetEntity = Algorithm.class)
	private Long sourceAlgorithm;
	
	@ManyToOne(targetEntity = Algorithm.class)
	private Long targetAlgorithm;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private AlgoRelationType algoRelationType;
	
	@Setter
	@Getter
	private String description;
	
}
