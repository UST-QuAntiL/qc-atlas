package org.planqk.atlas.web.linkassembler;

import org.planqk.atlas.web.dtos.FileDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

@Component
public class FileAssembler extends GenericLinkAssembler<FileDto> {

    @Override
    public void addLinks(EntityModel<FileDto> resource) {

    }

}
