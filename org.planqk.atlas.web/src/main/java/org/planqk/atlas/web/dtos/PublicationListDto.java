package org.planqk.atlas.web.dtos;

import lombok.Getter;
import org.assertj.core.util.Lists;

import java.util.List;

public class PublicationListDto {
    @Getter
    private final List<PublicationDto> publicationDtos = Lists.newArrayList();

    public void add(final List<PublicationDto> publications) {
        this.publicationDtos.addAll(publications);
    }

    public void add(final PublicationDto publication) {
        this.publicationDtos.add(publication);
    }
}
