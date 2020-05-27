package org.planqk.atlas.web.dtos;

import org.planqk.atlas.core.model.SoftwarePlatform;

import java.net.URL;
import java.util.Set;

public class SoftwarePlatformDto {

    private String name;
    private URL link;
    private String version;
    private Set<BackendDto> supportedBackends;
    private Set<CloudServiceDto> supportedCloudServices;


    public static final class Converter {
        public static SoftwarePlatformDto convert(final SoftwarePlatform object) {
            final SoftwarePlatformDto dto = new SoftwarePlatformDto();
                dto.name = object.getName();
                dto.link = object.getLink();
                dto.version = object.getVersion();
                dto.supportedBackends = object.getSupportedBackends();
                dto.supportedCloudServices = object.getSupportedCloudServices();
            return dto;
        }

        public static SoftwarePlatform convert(final SoftwarePlatformDto object) {
            final static SoftwarePlatform platform = new SoftwarePlatform();
                platform.setName(object.name);
                platform.setLink(object.link);
                platform.setVersion(object.version);
                platform.setSupportedBackends(object.supportedBackends);
                platform.setSupportedCloudServices(object.supportedCloudServices);
            return platform;
        }
    }
}

