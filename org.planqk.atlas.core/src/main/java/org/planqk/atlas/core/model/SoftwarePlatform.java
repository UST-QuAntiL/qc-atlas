package org.planqk.atlas.core.model;

import lombok.*;

import javax.persistence.*;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * A software platform e.g. qiskit has a number of supported backends and a number of supported cloud services which
 * also contain a number of supported backends. E.g. the software platform qiskit supports ibmq as a cloud service and
 * both support the backend ibmq_rome. However, a software platform might not support backends which can be used by a
 * supported cloud service and vice versa.
 */

@Entity
@Table(name = "software_platforms")
@Data
public class SoftwarePlatform extends HasId {

    private String name;
    private URL link;
    private String version;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE })
    @JoinTable(name = "software_platforms_backends", joinColumns = @JoinColumn(name = "software_platform_id"), inverseJoinColumns = @JoinColumn(name = "backend_id"))
    private Set<Backend> supportedBackends = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE })
    @JoinTable(name = "software_platform_cloud_services", joinColumns = @JoinColumn(name = "software_platform_id"), inverseJoinColumns = @JoinColumn(name = "cloud_service_id"))
    private Set<CloudService> supportedCloudServices = new HashSet<>();
}
