/*******************************************************************************
 * Copyright (c) 2020 the qc-atlas contributors.
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
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Entity which represents the publication.
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Data
public class Publication extends KnowledgeArtifact {

    private String doi;

    private String url;

    private String title;

    @ElementCollection
    private List<String> authors = new ArrayList<>();

    @ManyToMany(mappedBy = "publications",
        cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @EqualsAndHashCode.Exclude
    private Set<Algorithm> algorithms = new HashSet<>();

    @ManyToMany(mappedBy = "publications",
        cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @EqualsAndHashCode.Exclude
    private Set<Implementation> implementations = new HashSet<>();

    public void addAlgorithm(@NonNull Algorithm algorithm) {
        if (algorithms.contains(algorithm)) {
            return;
        }
        algorithms.add(algorithm);
        algorithm.addPublication(this);
    }

    public void removeAlgorithm(@NonNull Algorithm algorithm) {
        if (!algorithms.contains(algorithm)) {
            return;
        }
        algorithms.remove(algorithm);
        algorithm.removePublication(this);
    }

    public void addImplementation(@NonNull Implementation implementation) {
        if (implementations.contains(implementation)) {
            return;
        }
        implementations.add(implementation);
        implementation.addPublication(this);
    }

    public void removeImplementation(@NonNull Implementation implementation) {
        if (!implementations.contains(implementation)) {
            return;
        }
        implementations.remove(implementation);
        implementation.removePublication(this);
    }
}
