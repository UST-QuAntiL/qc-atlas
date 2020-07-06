package org.planqk.atlas.web.controller.mixin;

import java.util.Objects;

import org.planqk.atlas.core.model.ComputingResourceProperty;
import org.planqk.atlas.core.services.ComputingResourcePropertyService;
import org.planqk.atlas.web.dtos.ComputingResourcePropertyDto;
import org.planqk.atlas.web.exceptions.InvalidParameterException;
import org.planqk.atlas.web.utils.ModelMapperUtils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ComputingResourceMixin {
    private final ComputingResourcePropertyService computingResourcePropertyService;

    public ComputingResourceProperty fromDto(ComputingResourcePropertyDto resourceDto) {
        if (Objects.isNull(resourceDto.getType().getId())) {
            throw new InvalidParameterException("empty type ID");
        }
        var type = computingResourcePropertyService.findComputingResourcePropertyTypeById(resourceDto.getType().getId());
        var resource = ModelMapperUtils.convert(resourceDto, ComputingResourceProperty.class);
        resource.setComputingResourcePropertyType(type);
        return resource;
    }
}
