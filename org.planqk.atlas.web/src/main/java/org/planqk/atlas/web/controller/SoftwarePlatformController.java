package org.planqk.atlas.web.controller;

import org.planqk.atlas.core.model.SoftwarePlatform;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.annotation.ApiVersion;
import org.planqk.atlas.web.dtos.SoftwarePlatformDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
a
@io.swagger.v3.oas.annotations.tags.Tag(name = "software_platform")
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.SOFTWARE_PLATFORMS)
@ApiVersion("v1")
public class SoftwarePlatformController {
    final private static Logger LOG = LoggerFactory.getLogger(SoftwarePlatformController.class);

    public SoftwarePlatformController () {

    }

    public static SoftwarePlatformDto createSoftwarePlatformDto (SoftwarePlatform softwarePlatform) {
        SoftwarePlatformDto dto = SoftwarePlatformDto.Converter.convert(softwarePlatform);

        return dto;
    }

}
