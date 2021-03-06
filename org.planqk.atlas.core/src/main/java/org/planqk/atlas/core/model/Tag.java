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

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@EqualsAndHashCode
@NoArgsConstructor
@Entity
@Data
public class Tag {

    private String category;

    @Id
    private String value;

    @ManyToMany(mappedBy = "tags", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @EqualsAndHashCode.Exclude
    private Set<Algorithm> algorithms = new HashSet<>();

    @ManyToMany(mappedBy = "tags", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @EqualsAndHashCode.Exclude
    private Set<Implementation> implementations = new HashSet<>();

    public void addAlgorithm(@NonNull Algorithm algorithm) {
        if (algorithms.contains(algorithm)) {
            return;
        }
        algorithms.add(algorithm);
        algorithm.addTag(this);
    }

    public void removeAlgorithm(@NonNull Algorithm algorithm) {
        if (!algorithms.contains(algorithm)) {
            return;
        }
        algorithms.remove(algorithm);
        algorithm.removeTag(this);
    }

    public void addImplementation(@NonNull Implementation implementation) {
        if (implementations.contains(implementation)) {
            return;
        }
        implementations.add(implementation);
        implementation.addTag(this);
    }

    public void removeImplementation(@NonNull Implementation implementation) {
        if (!implementations.contains(implementation)) {
            return;
        }
        implementations.remove(implementation);
        implementation.removeTag(this);
    }
}
