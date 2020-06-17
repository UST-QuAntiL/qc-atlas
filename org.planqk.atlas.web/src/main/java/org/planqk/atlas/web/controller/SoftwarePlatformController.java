package org.planqk.atlas.web.controller;

import java.util.NoSuchElementException;
import java.util.UUID;

import javax.validation.Valid;

import org.planqk.atlas.core.model.SoftwarePlatform;
import org.planqk.atlas.core.services.SoftwarePlatformService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.annotation.ApiVersion;
import org.planqk.atlas.web.dtos.SoftwarePlatformDto;
import org.planqk.atlas.web.linkassembler.SoftwarePlatformAssembler;
import org.planqk.atlas.web.utils.HateoasUtils;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.RestUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
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

@io.swagger.v3.oas.annotations.tags.Tag(name = "software-platform")
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

    @Operation(responses = { @ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "404", content = @Content),
            @ApiResponse(responseCode = "500", content = @Content) })
    @GetMapping("/")
    public HttpEntity<PagedModel<EntityModel<SoftwarePlatformDto>>> getSoftwarePlatforms(@RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        Page<SoftwarePlatform> platforms = softwarePlatformService
                .findAll(RestUtils.getPageableFromRequestParams(page, size));
        Page<SoftwarePlatformDto> platformDtos = ModelMapperUtils.convertPage(platforms, SoftwarePlatformDto.class);
        PagedModel<EntityModel<SoftwarePlatformDto>> pagedPlatformDtos = paginationAssembler.toModel(platformDtos);
        softwarePlatformAssembler.addLinks(pagedPlatformDtos.getContent());
        return new ResponseEntity<>(pagedPlatformDtos, HttpStatus.OK);
    }

    @Operation(responses = { @ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "404", content = @Content),
            @ApiResponse(responseCode = "500", content = @Content) })
    @GetMapping("/{id}")
    public HttpEntity<EntityModel<SoftwarePlatformDto>> getSoftwarePlatform(@PathVariable UUID id) {
        SoftwarePlatformDto platformDto = ModelMapperUtils.convert(softwarePlatformService.findById(id),
                SoftwarePlatformDto.class);
        EntityModel<SoftwarePlatformDto> platformDtoEntity = HateoasUtils.generateEntityModel(platformDto);
        softwarePlatformAssembler.addLinks(platformDtoEntity);
        return new ResponseEntity<>(platformDtoEntity, HttpStatus.OK);
    }

    @Operation(responses = { @ApiResponse(responseCode = "201"), @ApiResponse(responseCode = "400", content = @Content),
            @ApiResponse(responseCode = "500", content = @Content) })
    @PostMapping("/")
    public HttpEntity<EntityModel<SoftwarePlatformDto>> addSoftwarePlatform(
            @Valid @RequestBody SoftwarePlatformDto platformDto) {
        SoftwarePlatform savedPlatform = softwarePlatformService
                .save(ModelMapperUtils.convert(platformDto, SoftwarePlatform.class));
        SoftwarePlatformDto savedPlatformDto = ModelMapperUtils.convert(savedPlatform, SoftwarePlatformDto.class);
        EntityModel<SoftwarePlatformDto> platformDtoEntity = HateoasUtils.generateEntityModel(savedPlatformDto);
        softwarePlatformAssembler.addLinks(platformDtoEntity);
        return new ResponseEntity<>(platformDtoEntity, HttpStatus.CREATED);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "404")})
    @PutMapping("/{id}")
    public HttpEntity<EntityModel<SoftwarePlatformDto>> updateSoftwarePlatform(@PathVariable UUID id,
                                                                            @Valid @RequestBody SoftwarePlatformDto softwarePlatformDto) {
        LOG.debug("Put to update software platform with id {}.", id);
        SoftwarePlatform updatedSoftwarePlatform = softwarePlatformService.update(id, ModelMapperUtils.convert(softwarePlatformDto, SoftwarePlatform.class));
        EntityModel<SoftwarePlatformDto> dtoOutput = HateoasUtils.generateEntityModel(ModelMapperUtils.convert(updatedSoftwarePlatform, SoftwarePlatformDto.class));
        softwarePlatformAssembler.addLinks(dtoOutput);
        return new ResponseEntity<>(dtoOutput, HttpStatus.OK);
    }

    @Operation(responses = { @ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "404", content = @Content),
            @ApiResponse(responseCode = "500", content = @Content) })
    @DeleteMapping("/{id}")
    public HttpEntity<SoftwarePlatformDto> deleteSoftwarePlatform(@PathVariable UUID id) {
        softwarePlatformService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNotFound() {
        return ResponseEntity.notFound().build();
    }
}
