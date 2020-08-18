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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.springframework.lang.NonNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entity representing a quantum algorithm, e.g., Shors factorization algorithm.
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Algorithm extends AlgorOrImpl implements ModelWithPublications {

    private String name;

    private String acronym;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "algorithm_publication",
            joinColumns = @JoinColumn(name = "algorithm_id"),
            inverseJoinColumns = @JoinColumn(name = "publication_id")
    )
    @EqualsAndHashCode.Exclude
    private Set<Publication> publications = new HashSet<>();

    @Column(columnDefinition = "text")
    private String intent;

    @Column(columnDefinition = "text")
    private String problem;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "sourceAlgorithm", referencedColumnName = "id")
    @EqualsAndHashCode.Exclude
    private Set<AlgorithmRelation> algorithmRelations = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "algorithm", orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    private Set<ComputeResourceProperty> requiredComputeResourceProperties = new HashSet<>();

    @Column(columnDefinition = "text")
    private String inputFormat;

    @Column(columnDefinition = "text")
    private String algoParameter;

    @Column(columnDefinition = "text")
    private String outputFormat;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sketch> sketches = new ArrayList<>();

    @Column(columnDefinition = "text")
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

    public void addComputeResourceProperty(@lombok.NonNull ComputeResourceProperty resource) {
        this.requiredComputeResourceProperties.add(resource);
    }

    public Set<Publication> getPublications() {
        return new HashSet<>(publications);
    }

    public void addPublication(Publication publication) {
        if (publications.contains(publication)) {
            return;
        }
        publications.add(publication);
        publication.addAlgorithm(this);
    }

    public void removePublication(Publication publication) {
        if (!publications.contains(publication)) {
            return;
        }
        publications.remove(publication);
        publication.removeAlgorithm(this);
    }

    public Set<ApplicationArea> getApplicationAreas() {
        return new HashSet<ApplicationArea>(applicationAreas);
    }

    public void addApplicationArea(ApplicationArea applicationArea) {
        if (applicationAreas.contains(applicationArea)) {
            return;
        }
        applicationAreas.add(applicationArea);
        applicationArea.addAlgorithm(this);
    }

    public void removeApplicationArea(ApplicationArea applicationArea) {
        if (!applicationAreas.contains(applicationArea)) {
            return;
        }
        applicationAreas.remove(applicationArea);
        applicationArea.removeAlgorithm(this);
    }

    public Set<ProblemType> getProblemTypes() {
        return new HashSet<ProblemType>(problemTypes);
    }

    public void addProblemType(ProblemType problemType) {
        if (problemTypes.contains(problemType)) {
            return;
        }
        problemTypes.add(problemType);
        problemType.addAlgorithm(this);
    }

    public void removeProblemType(ProblemType problemType) {
        if (!problemTypes.contains(problemType)) {
            return;
        }
        problemTypes.remove(problemType);
        problemType.removeAlgorithm(this);
    }



    public void addSketch(Sketch sketch) {
        sketches.add(sketch);
    }

    public void removeSketches(List<Sketch> sketches) {
        sketches.removeAll(sketches);
    }

}
