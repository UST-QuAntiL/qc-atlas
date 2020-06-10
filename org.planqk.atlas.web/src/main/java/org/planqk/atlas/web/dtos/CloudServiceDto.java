package org.planqk.atlas.web.dtos;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
public class CloudServiceDto extends RepresentationModel<CloudServiceDto> {

    private UUID id;

    @NotNull(message = "CloudService name must not be null!")
    private String name;

    private String provider;

    private URL url;

    private String costModel;

    private Set<BackendDto> providedBackends = new HashSet<>();

}
