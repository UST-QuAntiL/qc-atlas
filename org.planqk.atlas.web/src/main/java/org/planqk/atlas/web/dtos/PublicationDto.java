package org.planqk.atlas.web.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.List;
import java.util.UUID;

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

}
