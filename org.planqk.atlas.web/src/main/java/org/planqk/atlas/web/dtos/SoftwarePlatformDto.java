package org.planqk.atlas.web.dtos;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@JsonTypeName("CLASSIC")
@NoArgsConstructor
public class SoftwarePlatformDto extends RepresentationModel<SoftwarePlatformDto> {
    
    private UUID id;

    @NotNull(message = "SoftwarePlatform name must not be null!")
    private String name;

    private URL link;

    private String version;

    private Set<BackendDto> supportedBackends = new HashSet<>();

    private Set<CloudServiceDto> supportedCloudServices = new HashSet<>();

}

interface BackendDto {
}
