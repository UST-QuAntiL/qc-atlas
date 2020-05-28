package org.planqk.atlas.web.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.planqk.atlas.core.model.Backend;
import org.planqk.atlas.core.model.SoftwarePlatform;
import org.springframework.hateoas.RepresentationModel;

import java.net.URL;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@NoArgsConstructor
public class SoftwarePlatformDto extends RepresentationModel<SoftwarePlatformDto> {

    private UUID id;
    private String name;
    private URL link;
    private String version;
    private Set<BackendDto> supportedBackends;
    private Set<CloudServiceDto> supportedCloudServices;


    public static final class Converter {
        public static SoftwarePlatformDto convert(final SoftwarePlatform object) {
            final SoftwarePlatformDto dto = new SoftwarePlatformDto();
            dto.setId(object.getId());
            dto.setName(object.getName());
            dto.setLink(object.getLink());
            dto.setVersion(object.getVersion());

            dto.setSupportedBackends(object.getSupportedBackends()
                    .stream()
                    .map(BackendDto.Converter::convert)
                    .collect(Collectors.toSet()));

            dto.setSupportedCloudServices(object.getSupportedCloudServices()
                    .stream()
                    .map(CloudServiceDto.Converter::convert)
                    .collect(Collectors.toSet()));
            return dto;
        }

        public static SoftwarePlatform convert(final SoftwarePlatformDto object) {
            final SoftwarePlatform platform = new SoftwarePlatform();
            platform.setId(object.getId());
            platform.setName(object.getName());
            platform.setLink(object.getLink());
            platform.setVersion(object.getVersion());

            if (Objects.nonNull(object.getSupportedBackends())) {
                platform.setSupportedBackends(object.getSupportedBackends()
                        .stream()
                        .map(BackendDto.Converter::convert)
                        .collect(Collectors.toSet()));
            }

            if (Objects.nonNull(object.getSupportedCloudServices())) {
                platform.setSupportedCloudServices(object.getSupportedCloudServices()
                        .stream()
                        .map(CloudServiceDto.Converter::convert)
                        .collect(Collectors.toSet()));
            }

            return platform;
        }
    }
}

class BackendDto {
    static final class Converter {
        public static BackendDto convert(final Backend object) {
            return new BackendDto();
        }

        public static Backend convert(final BackendDto object) {
            return new Backend();
        }
    }
}
