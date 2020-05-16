package org.planqk.atlas.core.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import jdk.jfr.Name;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class AlgorithmRelation extends HasId {
	
	@ManyToOne
	private Algorithm sourceAlgorithm;
	
	@ManyToOne
	private Algorithm targetAlgorithm;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private AlgoRelationType algoRelationType;
	
	@Setter
	@Getter
	private String description;
	
}
