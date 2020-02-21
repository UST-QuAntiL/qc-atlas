package org.planqk.quality.api.dtos;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.util.Lists;
import org.springframework.hateoas.RepresentationModel;

/**
 * Data transfer object for multiple Implementations ({@link org.planqk.quality.model.Implementation}).
 */
public class ImplementationListDto extends RepresentationModel<ImplementationListDto> {

    private final List<ImplementationDto> implementationDtos = Lists.newArrayList();

    public List<ImplementationDto> getImplementations() {
        return this.implementationDtos;
    }

    public void add(final ImplementationDto... implementations) {
        this.implementationDtos.addAll(Arrays.asList(implementations));
    }
}
