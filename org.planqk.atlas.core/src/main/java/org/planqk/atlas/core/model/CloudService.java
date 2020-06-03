package org.planqk.atlas.core.model;

import lombok.Data;

import javax.persistence.*;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "cloud_services")
@Data
public class CloudService extends HasId {

    private String name;
    private String provider;
    private URL url;
    private String costModel;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.MERGE}
    )
    @JoinTable(
            name = "cloud_services_backends",
            joinColumns = @JoinColumn(name = "cloud_service_id"),
            inverseJoinColumns = @JoinColumn(name = "backend_id")
    )
    private Set<Backend> providedBackends = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE}, mappedBy = "supportedCloudServices")
    private Set<SoftwarePlatform> softwarePlatforms = new HashSet<>();
}
