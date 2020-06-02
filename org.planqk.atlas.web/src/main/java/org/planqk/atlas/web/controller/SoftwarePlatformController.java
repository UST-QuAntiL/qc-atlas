package org.planqk.atlas.web.controller;

import lombok.AllArgsConstructor;
import org.planqk.atlas.core.model.SoftwarePlatform;
import org.planqk.atlas.core.services.SoftwarePlatformService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.annotation.ApiVersion;
import org.planqk.atlas.web.dtos.SoftwarePlatformDto;
import org.planqk.atlas.web.linkassembler.SoftwarePlatformAssembler;
import org.planqk.atlas.web.utils.HateoasUtils;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.RestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.NoSuchElementException;
import java.util.UUID;


@io.swagger.v3.oas.annotations.tags.Tag(name = "software_platform")
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.SOFTWARE_PLATFORMS)
@ApiVersion("v1")
@AllArgsConstructor
public class SoftwarePlatformController {
    final private static Logger LOG = LoggerFactory.getLogger(SoftwarePlatformController.class);

    private final SoftwarePlatformService softwarePlatformService;
    private final SoftwarePlatformAssembler softwarePlatformAssembler;
    private final PagedResourcesAssembler<SoftwarePlatformDto> paginationAssembler;

    @GetMapping("/")
    public HttpEntity<?> getSoftwarePlatforms(@RequestParam(required = false) Integer page,
                                              @RequestParam(required = false) Integer size) {
        Page<SoftwarePlatform> platforms = softwarePlatformService.findAll(RestUtils.getPageableFromRequestParams(page, size));
        Page<SoftwarePlatformDto> platformDtos = ModelMapperUtils.convertPage(platforms, SoftwarePlatformDto.class);
        PagedModel<EntityModel<SoftwarePlatformDto>> pagedPlatformDtos = paginationAssembler.toModel(platformDtos);
        softwarePlatformAssembler.addLinks(pagedPlatformDtos.getContent());
        return new ResponseEntity<>(pagedPlatformDtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public HttpEntity<EntityModel<SoftwarePlatformDto>> getSoftwarePlatform(@PathVariable UUID id) {
        SoftwarePlatformDto platformDto = ModelMapperUtils.convert(softwarePlatformService.findById(id), SoftwarePlatformDto.class);
        EntityModel<SoftwarePlatformDto> platformDtoEntity = HateoasUtils.generateEntityModel(platformDto);
        softwarePlatformAssembler.addLinks(platformDtoEntity);
        return new ResponseEntity<>(platformDtoEntity, HttpStatus.OK);
    }

    @GetMapping("/echo")
    public HttpEntity<?> echo () {
        return ResponseEntity.ok("Tach");
    }

    @PutMapping("/")
    public HttpEntity<EntityModel<SoftwarePlatformDto>> addSoftwarePlatform(@Valid @RequestBody SoftwarePlatformDto platformDto) {
        SoftwarePlatform savedPlatform= softwarePlatformService.save(ModelMapperUtils.convert(platformDto, SoftwarePlatform.class));
        SoftwarePlatformDto savedPlatformDto = ModelMapperUtils.convert(savedPlatform, SoftwarePlatformDto.class);
        EntityModel<SoftwarePlatformDto> platformDtoEntity = HateoasUtils.generateEntityModel(savedPlatformDto);
        softwarePlatformAssembler.addLinks(platformDtoEntity);
        return new ResponseEntity<>(platformDtoEntity, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public HttpEntity<SoftwarePlatformDto> deleteSoftwarePlatform(@PathVariable UUID id) {
        softwarePlatformService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity handleNotFound() {
        return ResponseEntity.notFound().build();
    }
}
