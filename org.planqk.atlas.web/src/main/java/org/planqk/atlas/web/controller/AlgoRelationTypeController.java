package org.planqk.atlas.web.controller;

import java.util.UUID;

import javax.validation.Valid;

import org.planqk.atlas.core.model.AlgoRelationType;
import org.planqk.atlas.core.services.AlgoRelationTypeService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.AlgoRelationTypeDto;
import org.planqk.atlas.web.linkassembler.AlgoRelationTypeAssembler;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.RestUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

@io.swagger.v3.oas.annotations.tags.Tag(name = "algorithm-relation-type")
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.API_VERSION + "/" + Constants.ALGO_RELATION_TYPES)
@AllArgsConstructor
public class AlgoRelationTypeController {

    private AlgoRelationTypeService algoRelationTypeService;
    private AlgoRelationTypeAssembler algoRelationTypeAssembler;

    @Operation(responses = {@ApiResponse(responseCode = "201")}, description = "Custom ID will be ignored.")
    @PostMapping()
    public HttpEntity<EntityModel<AlgoRelationTypeDto>> createAlgoRelationType(
            @Valid @RequestBody AlgoRelationTypeDto algoRelationTypeDto) {
        var entityInput = ModelMapperUtils.convert(algoRelationTypeDto, AlgoRelationType.class);
        var savedAlgoRelationType = algoRelationTypeService.save(entityInput);
        return new ResponseEntity<>(algoRelationTypeAssembler.toModel(savedAlgoRelationType), HttpStatus.CREATED);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")}, description = "Custom ID will be ignored.")
    @PutMapping("/{id}")
    public HttpEntity<EntityModel<AlgoRelationTypeDto>> updateAlgoRelationType(@PathVariable UUID id,
                                                                               @Valid @RequestBody AlgoRelationTypeDto algoRelationTypeDto) {
        var entityInput = ModelMapperUtils.convert(algoRelationTypeDto, AlgoRelationType.class);
        var savedAlgoRelationType = algoRelationTypeService.update(id, entityInput);
        return ResponseEntity.ok(algoRelationTypeAssembler.toModel(savedAlgoRelationType));
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "404", description = "Algorithm relation with given id doesn't exist")})
    @DeleteMapping("/{id}")
    public HttpEntity<Void> deleteAlgoRelationType(@PathVariable UUID id) {
        // delete entity by id
        algoRelationTypeService.findById(id);
        algoRelationTypeService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @GetMapping()
    public HttpEntity<PagedModel<EntityModel<AlgoRelationTypeDto>>> getAlgoRelationTypes(
            @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        Pageable p = RestUtils.getPageableFromRequestParams(page, size);
        Page<AlgoRelationType> entities = algoRelationTypeService.findAll(p);
        return ResponseEntity.ok(algoRelationTypeAssembler.toModel(entities));
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @GetMapping("/{id}")
    public HttpEntity<EntityModel<AlgoRelationTypeDto>> getAlgoRelationTypeById(@PathVariable UUID id) {
        var algoRelationType = algoRelationTypeService.findById(id);
        return ResponseEntity.ok(algoRelationTypeAssembler.toModel(algoRelationType));
    }
}
