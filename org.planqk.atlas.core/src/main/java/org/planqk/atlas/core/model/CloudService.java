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

import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

/**
 * A cloud service is a service which provides backends and can be used by developers via a software platform which supports the cloud service. E.g.
 * qiskit is a software platform which supports the cloud service ibmq, which provides the backend ibmq_rome.
 */

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class CloudService extends HasId {

    private String name;

    private String provider;

    private URL url;

    private String description;

    private String costModel;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinTable(name = "cloud_services_compute_resources",
        joinColumns = @JoinColumn(name = "cloud_service_id"),
        inverseJoinColumns = @JoinColumn(name = "compute_resource_id"))
    private Set<ComputeResource> providedComputeResources = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY,
        cascade = {CascadeType.MERGE},
        mappedBy = "supportedCloudServices")
    private Set<SoftwarePlatform> softwarePlatforms = new HashSet<>();

    public void addSoftwarePlatform(@NonNull SoftwarePlatform softwarePlatform) {
        if (softwarePlatforms.contains(softwarePlatform)) {
            return;
        }
        softwarePlatforms.add(softwarePlatform);
        softwarePlatform.addCloudService(this);
    }

    public void removeSoftwarePlatform(@NonNull SoftwarePlatform softwarePlatform) {
        if (!softwarePlatforms.contains(softwarePlatform)) {
            return;
        }
        softwarePlatforms.remove(softwarePlatform);
        softwarePlatform.removeCloudService(this);
    }

    public void addComputeResource(@NonNull ComputeResource computeResource) {
        if (providedComputeResources.contains(computeResource)) {
            return;
        }
        providedComputeResources.add(computeResource);
        computeResource.addCloudService(this);
    }

    public void removeComputeResource(@NonNull ComputeResource computeResource) {
        if (!providedComputeResources.contains(computeResource)) {
            return;
        }
        providedComputeResources.remove(computeResource);
        computeResource.removeCloudService(this);
    }
}
