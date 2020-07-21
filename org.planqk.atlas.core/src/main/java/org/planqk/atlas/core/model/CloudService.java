package org.planqk.atlas.core.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

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
@Table(name = "cloud_services")
@Data
public class CloudService extends HasId {

    private String name;
    private String provider;
    private URL url;
    private String description;
    private String costModel;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE })
    @JoinTable(name = "cloud_services_backends", joinColumns = @JoinColumn(name = "cloud_service_id"), inverseJoinColumns = @JoinColumn(name = "backend_id"))
    private Set<ComputeResource> providedComputeResources = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE }, mappedBy = "supportedCloudServices")
    private Set<SoftwarePlatform> softwarePlatforms = new HashSet<>();
}
