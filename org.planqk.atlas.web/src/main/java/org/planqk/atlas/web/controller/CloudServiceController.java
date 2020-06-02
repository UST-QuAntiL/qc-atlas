package org.planqk.atlas.web.controller;

import lombok.AllArgsConstructor;
import org.planqk.atlas.core.model.CloudService;
import org.planqk.atlas.core.services.CloudServiceService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.annotation.ApiVersion;
import org.planqk.atlas.web.dtos.CloudServiceDto;
import org.planqk.atlas.web.linkassembler.CloudServiceAssembler;
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
import java.util.UUID;

@io.swagger.v3.oas.annotations.tags.Tag(name = "cloud_services")
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.CLOUD_SERVICES)
@ApiVersion("v1")
@AllArgsConstructor
public class CloudServiceController {
    final private static Logger LOG = LoggerFactory.getLogger(CloudServiceController.class);

    private final CloudServiceService cloudServiceService;
    private final CloudServiceAssembler cloudServiceAssembler;
    private final PagedResourcesAssembler<CloudServiceDto> paginationAssembler;

    @GetMapping("/")
    public HttpEntity<?> getCloudServices(@RequestParam(required = false) Integer page,
                                          @RequestParam(required = false) Integer size) {
        Page<CloudService> cloudServices = cloudServiceService.findAll(RestUtils.getPageableFromRequestParams(page, size));
        Page<CloudServiceDto> cloudServiceDtos = ModelMapperUtils.convertPage(cloudServices, CloudServiceDto.class);
        PagedModel<EntityModel<CloudServiceDto>> pagedCloudServiceDtos = paginationAssembler.toModel(cloudServiceDtos);
        cloudServiceAssembler.addLinks(pagedCloudServiceDtos.getContent());
        return new ResponseEntity<>(pagedCloudServiceDtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public HttpEntity<EntityModel<CloudServiceDto>> getCloudService(@PathVariable UUID id) {
        CloudServiceDto savedCloudServiceDto = ModelMapperUtils.convert(cloudServiceService.findById(id), CloudServiceDto.class);
        EntityModel<CloudServiceDto> cloudServiceDtoEntity = HateoasUtils.generateEntityModel(savedCloudServiceDto);
        cloudServiceAssembler.addLinks(cloudServiceDtoEntity);
        return new ResponseEntity<>(cloudServiceDtoEntity, HttpStatus.OK);
    }

    @PutMapping("/")
    public HttpEntity<EntityModel<CloudServiceDto>> addCloudService(@Valid @RequestBody CloudServiceDto cloudServiceDto) {
        CloudService savedCloudService = cloudServiceService.save(ModelMapperUtils.convert(cloudServiceDto, CloudService.class));
        CloudServiceDto savedCloudServiceDto = ModelMapperUtils.convert(savedCloudService, CloudServiceDto.class);
        EntityModel<CloudServiceDto> cloudServiceDtoEntity = HateoasUtils.generateEntityModel(savedCloudServiceDto);
        cloudServiceAssembler.addLinks(cloudServiceDtoEntity);
        return new ResponseEntity<>(cloudServiceDtoEntity, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public HttpEntity<CloudServiceDto> deleteCloudService(@PathVariable UUID id) {
        cloudServiceService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
