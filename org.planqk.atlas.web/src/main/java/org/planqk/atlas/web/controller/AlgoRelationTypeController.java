package org.planqk.atlas.web.controller;

import java.util.UUID;

import javax.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.planqk.atlas.core.model.AlgoRelationType;
import org.planqk.atlas.core.services.AlgoRelationTypeService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.annotation.ApiVersion;
import org.planqk.atlas.web.dtos.AlgoRelationTypeDto;
import org.planqk.atlas.web.linkassembler.AlgoRelationTypeAssembler;
import org.planqk.atlas.web.utils.HateoasUtils;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.RestUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
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

import lombok.AllArgsConstructor;

@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.ALGO_RELATION_TYPES)
@ApiVersion("v1")
@AllArgsConstructor
public class AlgoRelationTypeController {

    private AlgoRelationTypeService algoRelationTypeService;
    private PagedResourcesAssembler<AlgoRelationTypeDto> paginationAssembler;
    private AlgoRelationTypeAssembler algoRelationTypeAssembler;

    @Operation(responses = {@ApiResponse(responseCode = "201")})
    @PostMapping("/")
    public HttpEntity<EntityModel<AlgoRelationTypeDto>> createAlgoRelationType(
            @Valid @RequestBody AlgoRelationTypeDto algoRelationTypeDto) {
        // Convert DTO to entity
        AlgoRelationType entityInput = ModelMapperUtils.convert(algoRelationTypeDto, AlgoRelationType.class);
        // save entity
        AlgoRelationType savedAlgoRelationType = algoRelationTypeService.save(entityInput);
        // convert entity to DTO
        AlgoRelationTypeDto dtoOutput = ModelMapperUtils.convert(savedAlgoRelationType, AlgoRelationTypeDto.class);
        // generate EntitiyModel
        EntityModel<AlgoRelationTypeDto> entityDto = HateoasUtils.generateEntityModel(dtoOutput);
        algoRelationTypeAssembler.addLinks(entityDto);
        return new ResponseEntity<>(entityDto, HttpStatus.CREATED);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @PutMapping("/{id}")
    public HttpEntity<EntityModel<AlgoRelationTypeDto>> updateAlgoRelationType(@PathVariable UUID id,
                                                                               @Valid @RequestBody AlgoRelationTypeDto algoRelationTypeDto) {
        // Convert DTO to entity
        AlgoRelationType entityInput = ModelMapperUtils.convert(algoRelationTypeDto, AlgoRelationType.class);
        // update entity
        AlgoRelationType savedAlgoRelationType = algoRelationTypeService.update(id, entityInput);
        // convert entity to DTO
        AlgoRelationTypeDto dtoOutput = ModelMapperUtils.convert(savedAlgoRelationType, AlgoRelationTypeDto.class);
        // generate EntitiyModel
        EntityModel<AlgoRelationTypeDto> entityDto = HateoasUtils.generateEntityModel(dtoOutput);
        algoRelationTypeAssembler.addLinks(entityDto);
        return new ResponseEntity<>(entityDto, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @DeleteMapping("/{id}")
    public HttpEntity<AlgoRelationTypeDto> deleteAlgoRelationType(@PathVariable UUID id) {
        // delete entity by id
        algoRelationTypeService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @GetMapping("/")
    public HttpEntity<PagedModel<EntityModel<AlgoRelationTypeDto>>> getAlgoRelationTypes(@RequestParam(required = false) Integer page,
                                                                                         @RequestParam(required = false) Integer size) {
        // Generate pageable
        Pageable p = RestUtils.getPageableFromRequestParams(page, size);
        // Get entities
        Page<AlgoRelationType> entities = algoRelationTypeService.findAll(p);
        // convert to DTO pageable
        Page<AlgoRelationTypeDto> dtos = ModelMapperUtils.convertPage(entities, AlgoRelationTypeDto.class);
        // generate paged model with links
        PagedModel<EntityModel<AlgoRelationTypeDto>> pagedEntityOutput = paginationAssembler.toModel(dtos);
        // add links
        algoRelationTypeAssembler.addLinks(pagedEntityOutput.getContent());
        return new ResponseEntity<>(pagedEntityOutput, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @GetMapping("/{id}")
    public HttpEntity<EntityModel<AlgoRelationTypeDto>> getAlgoRelationTypeById(@PathVariable UUID id) {
        AlgoRelationType algoRelationType = algoRelationTypeService.findById(id);
        // convert entity to DTO
        AlgoRelationTypeDto dtoOutput = ModelMapperUtils.convert(algoRelationType, AlgoRelationTypeDto.class);
        // generate EntitiyModel
        EntityModel<AlgoRelationTypeDto> entityDto = HateoasUtils.generateEntityModel(dtoOutput);
        algoRelationTypeAssembler.addLinks(entityDto);
        return new ResponseEntity<>(entityDto, HttpStatus.OK);
    }
}
