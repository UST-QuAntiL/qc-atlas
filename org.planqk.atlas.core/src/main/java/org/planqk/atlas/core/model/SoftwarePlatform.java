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

/**
 * A software platform e.g. qiskit has a number of supported backends and a number of supported cloud services which
 * also contain a number of supported backends. E.g. the software platform qiskit supports ibmq as a cloud service and
 * both support the backend ibmq_rome. However, a software platform might not support backends which can be used by a
 * supported cloud service and vice versa.
 */

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class SoftwarePlatform extends HasId {

    private String name;
    private URL link;
    private String licence;
    private String version;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinTable(name = "software_platforms_compute_resources",
            joinColumns = @JoinColumn(name = "software_platform_id"),
            inverseJoinColumns = @JoinColumn(name = "compute_resource_id"))
    private Set<ComputeResource> supportedComputeResources = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinTable(name = "software_platform_cloud_services",
            joinColumns = @JoinColumn(name = "software_platform_id"),
            inverseJoinColumns = @JoinColumn(name = "cloud_service_id"))
    private Set<CloudService> supportedCloudServices = new HashSet<>();

    @ManyToMany(mappedBy = "softwarePlatforms",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @EqualsAndHashCode.Exclude
    private Set<Implementation> implementations = new HashSet<>();

    public Set<Implementation> getImplementations() {
        return new HashSet<Implementation>(implementations);
    }

    public void addImplementation(Implementation implementation) {
        if (implementations.contains(implementation)) {
            return;
        }
        implementations.add(implementation);
        implementation.addSoftwarePlatform(this);
    }

    public void removeImplementation(Implementation implementation) {
        if (!implementations.contains(implementation)) {
            return;
        }
        implementations.remove(implementation);
        implementation.removeSoftwarePlatform(this);
    }
}
