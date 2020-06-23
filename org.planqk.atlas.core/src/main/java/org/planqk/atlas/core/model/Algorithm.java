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
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

/**
 * Entity representing a quantum algorithm, e.g., Shors factorization algorithm.
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Algorithm extends AlgorOrImpl {

    private String name;
    private String acronym;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "algorithm_publication",
            joinColumns = @JoinColumn(name = "publication_id"),
            inverseJoinColumns = @JoinColumn(name = "algorithm_id")
    )
    @EqualsAndHashCode.Exclude
    private Set<Publication> publications = new HashSet<>();

    private String intent;
    private String problem;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "sourceAlgorithm", referencedColumnName = "id")
    @EqualsAndHashCode.Exclude
    private Set<AlgorithmRelation> algorithmRelations = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "algorithm", orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    private Set<ComputingResource> requiredComputingResources = new HashSet<>();

    private String inputFormat;
    private String algoParameter;
    private String outputFormat;
    private Sketch sketch;
    private String solution;
    private String assumptions;
    private ComputationModel computationModel;

    @OneToMany(mappedBy = "algorithm", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    private Set<PatternRelation> relatedPatterns = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "algorithm_problem_type",
            joinColumns = @JoinColumn(name = "algorithm_id"),
            inverseJoinColumns = @JoinColumn(name = "problem_type_id"))
    @EqualsAndHashCode.Exclude
    private Set<ProblemType> problemTypes = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "algorithm_application_area",
            joinColumns = @JoinColumn(name = "algorithm_id"),
            inverseJoinColumns = @JoinColumn(name = "application_area_id"))
    @EqualsAndHashCode.Exclude
    private Set<ApplicationArea> applicationAreas = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(name = "algorithm_tag", joinColumns = @JoinColumn(name = "algorithm_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @EqualsAndHashCode.Exclude
    private Set<Tag> tags = new HashSet<>();

    @NonNull
    public boolean addAlgorithmRelation(AlgorithmRelation relation) {
        return algorithmRelations.add(relation);
    }

    public void setAlgorithmRelations(Set<AlgorithmRelation> algorithmRelations) {
        this.algorithmRelations.clear();
        if (algorithmRelations != null) {
            this.algorithmRelations.addAll(algorithmRelations);
        }
    }

    public void setRelatedPatterns(Set<PatternRelation> relatedPatterns) {
        this.relatedPatterns.clear();
        if (relatedPatterns != null) {
            this.relatedPatterns.addAll(relatedPatterns);
        }
    }

    public void addComputingResource(@lombok.NonNull ComputingResource resource) {
        this.requiredComputingResources.add(resource);
    }
}
