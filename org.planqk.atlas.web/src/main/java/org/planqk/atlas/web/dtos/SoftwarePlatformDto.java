package org.planqk.atlas.web.dtos;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@ToString(callSuper = true)
@Data
@NoArgsConstructor
public class SoftwarePlatformDto {

    private UUID id;

    @NotNull(message = "SoftwarePlatform name must not be null!")
    private String name;

    private URL link;

    private String version;

    private String licence;

    private Set<BackendDto> supportedBackends = new HashSet<>();

    private Set<CloudServiceDto> supportedCloudServices = new HashSet<>();

}
