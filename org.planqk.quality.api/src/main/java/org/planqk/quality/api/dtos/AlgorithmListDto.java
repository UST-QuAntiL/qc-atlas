package org.planqk.quality.api.dtos;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.util.Lists;
import org.springframework.hateoas.RepresentationModel;

/**
 * Data transfer object for multiple Algorithms ({@link org.planqk.quality.model.Algorithm}).
 */
public class AlgorithmListDto extends RepresentationModel<AlgorithmListDto> {

    private final List<AlgorithmDto> algorithmDtos = Lists.newArrayList();

    public List<AlgorithmDto> getAlgorithms() {
        return this.algorithmDtos;
    }

    public void add(final AlgorithmDto... algorithms) {
        this.algorithmDtos.addAll(Arrays.asList(algorithms));
    }
}
