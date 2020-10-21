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
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class ProblemType extends HasId {

    private String name;

    private UUID parentProblemType;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ManyToMany(mappedBy = "problemTypes")
    @EqualsAndHashCode.Exclude
    private Set<Algorithm> algorithms = new HashSet<>();

    public void addAlgorithm(@NonNull Algorithm algorithm) {
        if (algorithms.contains(algorithm)) {
            return;
        }
        algorithms.add(algorithm);
        algorithm.addProblemType(this);
    }

    public void removeAlgorithm(@NonNull Algorithm algorithm) {
        if (!algorithms.contains(algorithm)) {
            return;
        }
        algorithms.remove(algorithm);
        algorithm.removeProblemType(this);
    }
}
