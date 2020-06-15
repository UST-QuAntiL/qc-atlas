package org.planqk.atlas.web.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@EqualsAndHashCode
@Data
@NoArgsConstructor
public class PublicationDto {

    private UUID id;

    @NotNull(message = "Title of the Publication must not be null!")
    private String title;

    private String doi;

    @URL(message = "Publication URL must be a valid URL!")
    private String url;

    @NotEmpty(message = "Authors of the Publication must not be empty!")
    private List<String> authors;
}
