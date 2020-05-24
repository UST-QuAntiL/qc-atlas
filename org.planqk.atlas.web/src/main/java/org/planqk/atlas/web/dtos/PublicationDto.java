package org.planqk.atlas.web.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class PublicationDto extends RepresentationModel<PublicationDto> {

    private UUID id;
    @NotNull(message = "Publication Name must not be null!")
    private String title;
    private String doi;
    private URL url;
    @NotNull(message = "Authors of a publication must not be null!")
    private List<String> authors;

}
