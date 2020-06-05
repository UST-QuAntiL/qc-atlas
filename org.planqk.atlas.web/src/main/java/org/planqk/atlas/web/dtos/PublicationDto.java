package org.planqk.atlas.web.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.planqk.atlas.core.model.Algorithm;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.WRITE_ONLY;

@EqualsAndHashCode
@Data
@NoArgsConstructor
public class PublicationDto{

    private UUID id;

    @NotNull(message = "Title of the Publication must not be null!")
    private String title;

    private String doi;

    private URL url;

    @NotEmpty(message = "Authors of the Publication must not be empty!")
    private List<String> authors;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(accessMode= WRITE_ONLY)
    private Set<Algorithm> algorithms;
}
