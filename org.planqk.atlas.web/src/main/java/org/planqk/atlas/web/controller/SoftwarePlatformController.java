package org.planqk.atlas.web.controller;

import org.planqk.atlas.core.model.SoftwarePlatform;
import org.planqk.atlas.core.services.SoftwarePlatformService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.annotation.ApiVersion;
import org.planqk.atlas.web.dtos.SoftwarePlatformDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@io.swagger.v3.oas.annotations.tags.Tag(name = "software_platform")
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.SOFTWARE_PLATFORMS)
@ApiVersion("v1")
public class SoftwarePlatformController {
    final private static Logger LOG = LoggerFactory.getLogger(SoftwarePlatformController.class);

    private SoftwarePlatformService softwarePlatformService;

    public SoftwarePlatformController (SoftwarePlatformService softwarePlatformService) {
        this.softwarePlatformService = softwarePlatformService;
    }

    public static SoftwarePlatformDto createSoftwarePlatformDto (SoftwarePlatform softwarePlatform) {
        SoftwarePlatformDto dto = SoftwarePlatformDto.Converter.convert(softwarePlatform);
        dto.add(linkTo(methodOn(SoftwarePlatformController.class).getSoftwarePlatform(softwarePlatform.getId())).withSelfRel());
        return dto;
    }

    @GetMapping("/")
    public HttpEntity<SoftwarePlatformDto> getSoftwarePlatforms(@RequestParam(required = false) Integer page,
                                                                @RequestParam(required = false) Integer size) {
        return null;
    }

    @GetMapping("/{id}")
    public HttpEntity<SoftwarePlatformDto> getSoftwarePlatform(@PathVariable UUID id) {
        return null;
    }

    @PutMapping("/")
    public HttpEntity<SoftwarePlatformDto> addSoftwarePlatform(@RequestBody SoftwarePlatformDto platform) {
        return null;
    }
}
