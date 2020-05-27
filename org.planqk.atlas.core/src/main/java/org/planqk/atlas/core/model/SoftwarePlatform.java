package org.planqk.atlas.core.model;


import lombok.*;

import javax.persistence.*;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "software_platforms")
@Data
public class SoftwarePlatform extends HasId {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private URL link;

    @Getter
    @Setter
    private String version;

    @Getter
    @Setter
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinTable(
            name = "software_platforms_backends",
            joinColumns = @JoinColumn(name = "software_platform_id"),
            inverseJoinColumns = @JoinColumn(name = "backend_id")
    )
    private Set<Backend> supportedBackends = new HashSet<>();

    @Getter
    @Setter
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinTable(
            name = "software_platform_cloud_services",
            joinColumns = @JoinColumn(name = "software_platform_id"),
            inverseJoinColumns = @JoinColumn(name = "cloud_platform_id")
    )
    private Set<CloudService> supportedCloudServices = new HashSet<>();

}
