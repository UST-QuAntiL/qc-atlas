package org.planqk.atlas.web.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.planqk.atlas.core.model.Publication;
import org.springframework.hateoas.RepresentationModel;

import java.net.URL;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class PublicationDto extends RepresentationModel<PublicationDto> {

    private UUID id;
    private String title;
    private String doi;
    private URL url;
    private List<String> authors;

}
