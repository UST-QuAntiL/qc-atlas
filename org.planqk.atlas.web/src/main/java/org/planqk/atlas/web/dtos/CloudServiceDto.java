package org.planqk.atlas.web.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.planqk.atlas.core.model.CloudService;
import org.springframework.hateoas.RepresentationModel;

import java.net.URL;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@NoArgsConstructor
public class CloudServiceDto extends RepresentationModel<CloudServiceDto> {

    private UUID id;
    private String name;
    private String provider;
    private URL url;
    private String costModel;
    private Set<BackendDto> providedBackends = new HashSet<>();

    public static final class Converter {
        public static CloudServiceDto convert(final CloudService object) {
            final CloudServiceDto dto = new CloudServiceDto();
            dto.setId(object.getId());
            dto.setName(object.getName());
            dto.setProvider(object.getProvider());
            dto.setUrl(object.getUrl());
            dto.setCostModel(object.getCostModel());
            dto.setProvidedBackends(object.getProvidedBackends()
                    .stream()
                    .map(BackendDto.Converter::convert)
                    .collect(Collectors.toSet()));
            return dto;
        }

        public static CloudService convert(final CloudServiceDto object) {
            final CloudService cloudService = new CloudService();
            cloudService.setId(object.getId());
            cloudService.setName(object.getName());
            cloudService.setProvider(object.getProvider());
            cloudService.setUrl(object.getUrl());
            cloudService.setCostModel(object.getCostModel());

            if (Objects.nonNull(cloudService.getProvidedBackends())) {
                cloudService.setProvidedBackends(object.getProvidedBackends()
                        .stream()
                        .map(BackendDto.Converter::convert)
                        .collect(Collectors.toSet()));
            }

            return cloudService;
        }
    }
}