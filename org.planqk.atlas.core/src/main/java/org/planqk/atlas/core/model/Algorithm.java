/*******************************************************************************
 * Copyright (c) 2020 University of Stuttgart
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package org.planqk.atlas.core.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

/**
 * Entity representing a quantum algorithm, e.g., Shors factorization algorithm.
 */
@Entity
public class Algorithm extends AlgorOrImpl {
	
	@Setter
	@Getter
	private String name;
	
	@Setter
	@Getter
	private String acronym;
	
	@Setter
	@Getter
	private String intent;
	
	@Setter
	@Getter
	private String problem;
	
	@OneToMany(mappedBy = "sourceAlgorithm", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@Setter
	private Set<AlgorithmRelation> algorithmRelations;
	
	@Setter
	@Getter
	private String inputFormat;
	
	@Setter
	@Getter
	private String algoParameter;
	
	@Setter
	@Getter
	private String outputFormat;
	
	@Setter
	@Getter
	private Sketch sketch;
	
	@Setter
	@Getter
	private String assumptions;
	
	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "algorithm_problem_type",
            joinColumns = @JoinColumn(name = "algorithm_id"),
            inverseJoinColumns = @JoinColumn(name = "problem_type_id"))
	@Setter
	private Set<ProblemType> problemTypes;
	
	@ElementCollection
	@Setter
    private Set<String> applicationAreas;
	
	@OneToMany(mappedBy = "algorithm", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@Setter
	private Set<PatternRelation> relatedPatterns;

    @OneToMany(mappedBy = "implementedAlgorithm", cascade = {CascadeType.MERGE})
    @Setter
    private Set<Implementation> implementations;

    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "algorithm_tag",
            joinColumns = @JoinColumn(name = "algorithm_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @Setter
    private Set<Tag> tags;

    public Algorithm() {
        super();
    }

    @NonNull
    public Set<Implementation> getImplementations() {
        if (Objects.isNull(implementations)) {
            return new HashSet<>();
        }
        return implementations;
    }

    @NonNull
    public Set<Tag> getTags() {
        if (Objects.isNull(tags)) {
            return new HashSet<>();
        }
        return tags;
    }
    
    @NonNull
    public Set<AlgorithmRelation> getAlgorithmRelations() {
        if (Objects.isNull(algorithmRelations)) {
            return new HashSet<>();
        }
        return algorithmRelations;
    }
    
    @NonNull
    public Set<PatternRelation> getRelatedPatterns() {
        if (Objects.isNull(relatedPatterns)) {
            return new HashSet<>();
        }
        return relatedPatterns;
    }
    
    @NonNull
    public Set<String> getApplicationAreas() {
        if (Objects.isNull(applicationAreas)) {
            return new HashSet<>();
        }
        return applicationAreas;
    }
    
    @NonNull
    public Set<ProblemType> getProblemTypes() {
        if (Objects.isNull(problemTypes)) {
            return new HashSet<>();
        }
        return problemTypes;
    }
}
