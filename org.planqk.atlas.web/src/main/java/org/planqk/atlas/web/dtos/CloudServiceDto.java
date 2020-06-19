package org.planqk.atlas.web.dtos;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString(callSuper = true)
@Data
@NoArgsConstructor
public class CloudServiceDto {

    private UUID id;

    @NotNull(message = "CloudService name must not be null!")
    private String name;

    private String provider;
    @Schema(description = "URL", example = "https://www.ibm.com/quantum-computing/", required = false)
    private URL url;

    private String description;

    private String costModel;

    private Set<BackendDto> providedBackends = new HashSet<>();

}
