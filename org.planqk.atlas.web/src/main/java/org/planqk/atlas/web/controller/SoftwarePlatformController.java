package org.planqk.atlas.web.controller;

import java.util.UUID;

import javax.validation.Valid;

import org.planqk.atlas.core.model.SoftwarePlatform;
import org.planqk.atlas.core.services.SoftwarePlatformService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.SoftwarePlatformDto;
import org.planqk.atlas.web.linkassembler.SoftwarePlatformAssembler;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.RestUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@io.swagger.v3.oas.annotations.tags.Tag(name = "software-platform")
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.API_VERSION + "/" + Constants.SOFTWARE_PLATFORMS)
@AllArgsConstructor
public class SoftwarePlatformController {
    final private static Logger LOG = LoggerFactory.getLogger(SoftwarePlatformController.class);

    private final SoftwarePlatformService softwarePlatformService;
    private final SoftwarePlatformAssembler softwarePlatformAssembler;

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "404", content = @Content),
            @ApiResponse(responseCode = "500", content = @Content)})
    @GetMapping()
    public HttpEntity<PagedModel<EntityModel<SoftwarePlatformDto>>> getSoftwarePlatforms(@RequestParam(required = false) Integer page,
                                                                                         @RequestParam(required = false) Integer size) {
        var platforms = softwarePlatformService.findAll(RestUtils.getPageableFromRequestParams(page, size));
        return ResponseEntity.ok(softwarePlatformAssembler.toModel(platforms));
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "404", content = @Content),
            @ApiResponse(responseCode = "500", content = @Content)})
    @GetMapping("/{id}")
    public HttpEntity<EntityModel<SoftwarePlatformDto>> getSoftwarePlatform(@PathVariable UUID id) {
        var softwarePlatform = softwarePlatformService.findById(id);
        return ResponseEntity.ok(softwarePlatformAssembler.toModel(softwarePlatform));
    }

    @Operation(responses = {@ApiResponse(responseCode = "201"), @ApiResponse(responseCode = "400", content = @Content),
            @ApiResponse(responseCode = "500", content = @Content)}, description = "Custom ID will be ignored.")
    @PostMapping()
    public HttpEntity<EntityModel<SoftwarePlatformDto>> addSoftwarePlatform(
            @Valid @RequestBody SoftwarePlatformDto platformDto) {
        var savedPlatform = softwarePlatformService.save(ModelMapperUtils.convert(platformDto, SoftwarePlatform.class));
        return new ResponseEntity<>(softwarePlatformAssembler.toModel(savedPlatform), HttpStatus.CREATED);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "404")}, description = "Custom ID will be ignored.")
    @PutMapping("/{id}")
    public HttpEntity<EntityModel<SoftwarePlatformDto>> updateSoftwarePlatform(@PathVariable UUID id,
                                                                               @Valid @RequestBody SoftwarePlatformDto softwarePlatformDto) {
        LOG.debug("Put to update software platform with id {}.", id);
        var softwarePlatform = softwarePlatformService.update(id, ModelMapperUtils.convert(softwarePlatformDto, SoftwarePlatform.class));
        return ResponseEntity.ok(softwarePlatformAssembler.toModel(softwarePlatform));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Software platform with given id doesn't exist")})
    @DeleteMapping("/{id}")
    public HttpEntity<Void> deleteSoftwarePlatform(@PathVariable UUID id) {
        SoftwarePlatform softwarePlatform = softwarePlatformService.findById(id);
        softwarePlatform.getImplementations().forEach(implementation -> implementation.removeSoftwarePlatform(softwarePlatform));
        softwarePlatformService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
