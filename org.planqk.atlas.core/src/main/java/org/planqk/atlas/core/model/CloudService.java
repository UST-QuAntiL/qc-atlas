package org.planqk.atlas.core.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * A cloud service is a service which provides backends and can be used by developers via a software platform which
 * supports the cloud service. E.g. qiskit is a software platform which supports the cloud service ibmq, which provides
 * the backend ibmq_rome.
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
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE}, mappedBy = "supportedCloudServices")
    private Set<SoftwarePlatform> softwarePlatforms = new HashSet<>();

    public void addSoftwarePlatform(SoftwarePlatform softwarePlatform) {
        if (softwarePlatforms.contains(softwarePlatform)) {
            return;
        }
        softwarePlatforms.add(softwarePlatform);
        softwarePlatform.addCloudService(this);
    }

    public void removeSoftwarePlatform(SoftwarePlatform softwarePlatform) {
        if (!softwarePlatforms.contains(softwarePlatform)) {
            return;
        }
        softwarePlatforms.remove(softwarePlatform);
        softwarePlatform.removeCloudService(this);
    }

    public void addComputeResource(ComputeResource computeResource) {
        if (providedComputeResources.contains(computeResource)) {
            return;
        }
        providedComputeResources.add(computeResource);
        computeResource.addCloudService(this);
    }

    public void removeComputeResource(ComputeResource computeResource) {
        if (!providedComputeResources.contains(computeResource)) {
            return;
        }
        providedComputeResources.remove(computeResource);
        computeResource.removeCloudService(this);
    }
}
