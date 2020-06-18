package org.planqk.atlas.web.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@ToString(callSuper = true)
@Data
@NoArgsConstructor
public class CloudServiceDto {

    private UUID id;

    @NotNull(message = "CloudService name must not be null!")
    private String name;

    private String provider;

    private URL url;

    private String description;

    private String costModel;

    private Set<BackendDto> providedBackends = new HashSet<>();

}
