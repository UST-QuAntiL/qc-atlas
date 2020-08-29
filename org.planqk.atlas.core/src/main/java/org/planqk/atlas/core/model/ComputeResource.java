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
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

/**
 * A compute resource is a QPU or a Simulator which are both able to run Quantum Algorithms. E.g. ibmq_rome or
 * qasm_simulator.
 */

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class ComputeResource extends HasId {

    private String name;
    private String vendor;
    private String technology;

    @OneToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.ALL},
            mappedBy = "computeResource",
            orphanRemoval = true)
    private Set<ComputeResourceProperty> providedComputingResourceProperties = new HashSet<>();

    private QuantumComputationModel quantumComputationModel;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.MERGE},
            mappedBy = "supportedComputeResources")
    private Set<SoftwarePlatform> softwarePlatforms = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.MERGE},
            mappedBy = "providedComputeResources")
    private Set<CloudService> cloudServices = new HashSet<>();

    public void addSoftwarePlatform(@NonNull SoftwarePlatform softwarePlatform) {
        if (softwarePlatforms.contains(softwarePlatform)) {
            return;
        }
        softwarePlatforms.add(softwarePlatform);
        softwarePlatform.addComputeResource(this);
    }

    public void removeSoftwarePlatform(@NonNull SoftwarePlatform softwarePlatform) {
        if (!softwarePlatforms.contains(softwarePlatform)) {
            return;
        }
        softwarePlatforms.remove(softwarePlatform);
        softwarePlatform.removeComputeResource(this);
    }

    public void addCloudService(@NonNull CloudService cloudService) {
        if (cloudServices.contains(cloudService)) {
            return;
        }
        cloudServices.add(cloudService);
        cloudService.addComputeResource(this);
    }

    public void removeCloudService(@NonNull CloudService cloudService) {
        if (!cloudServices.contains(cloudService)) {
            return;
        }
        cloudServices.remove(cloudService);
        cloudService.removeComputeResource(this);
    }
}
