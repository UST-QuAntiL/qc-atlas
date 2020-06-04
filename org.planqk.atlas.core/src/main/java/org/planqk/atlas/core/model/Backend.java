package org.planqk.atlas.core.model;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "backends")
@Data
public class Backend extends HasId {

    private String name;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "supportedBackends")
    private Set<SoftwarePlatform> softwarePlatforms = new HashSet<>();
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "providedBackends")
    private Set<CloudService> cloudServices = new HashSet<>();
}
