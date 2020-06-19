package org.planqk.atlas.web.dtos;

import java.net.URL;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString(callSuper = true)
@Data
@NoArgsConstructor
public class SoftwarePlatformDto {

    private UUID id;

    @NotNull(message = "SoftwarePlatform name must not be null!")
    private String name;

    @Js
    private URL link;

    private String version;

    private String licence;

//    private Set<BackendDto> supportedBackends = new HashSet<>();
//
//    private Set<CloudServiceDto> supportedCloudServices = new HashSet<>();
}
