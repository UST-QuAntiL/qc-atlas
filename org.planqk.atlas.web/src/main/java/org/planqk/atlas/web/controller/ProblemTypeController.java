package org.planqk.atlas.web.controller;

import java.util.UUID;

import javax.validation.Valid;

import org.planqk.atlas.core.model.ProblemType;
import org.planqk.atlas.core.services.ProblemTypeService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.ProblemTypeDto;
import org.planqk.atlas.web.linkassembler.ProblemTypeAssembler;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.RestUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
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

@io.swagger.v3.oas.annotations.tags.Tag(name = "problem-type")
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.API_VERSION + "/" + Constants.PROBLEM_TYPES)
@AllArgsConstructor
@Slf4j
public class ProblemTypeController {

    private ProblemTypeService problemTypeService;
    private ProblemTypeAssembler problemTypeAssembler;

    @Operation(responses = {@ApiResponse(responseCode = "201")}, description = "Custom ID will be ignored.")
    @PostMapping()
    public HttpEntity<EntityModel<ProblemTypeDto>> createProblemType(
            @Valid @RequestBody ProblemTypeDto problemTypeDto) {
        var entityInput = ModelMapperUtils.convert(problemTypeDto, ProblemType.class);
        var savedProblemType = problemTypeService.save(entityInput);
        return new ResponseEntity<>(problemTypeAssembler.toModel(savedProblemType), HttpStatus.CREATED);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")}, description = "Custom ID will be ignored.")
    @PutMapping("/{id}")
    public HttpEntity<EntityModel<ProblemTypeDto>> updateProblemType(@PathVariable UUID id,
                                                                     @Valid @RequestBody ProblemTypeDto problemTypeDto) {
        var entityInput = ModelMapperUtils.convert(problemTypeDto, ProblemType.class);
        var updatedEntity = problemTypeService.update(id, entityInput);
        return ResponseEntity.ok(problemTypeAssembler.toModel(updatedEntity));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Problem type with given id doesn't exist")
    })
    @DeleteMapping("/{id}")
    public HttpEntity<Void> deleteProblemType(@PathVariable UUID id) {
        ProblemType problemType = problemTypeService.findById(id);
        problemType.getAlgorithms().forEach(algorithm -> algorithm.removeProblemType(problemType));
        problemTypeService.delete(problemType);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @GetMapping()
    public HttpEntity<PagedModel<EntityModel<ProblemTypeDto>>> getProblemTypes(
            @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        Pageable p = RestUtils.getPageableFromRequestParams(page, size);
        var entities = problemTypeService.findAll(p);
        return ResponseEntity.ok(problemTypeAssembler.toModel(entities));
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @GetMapping("/{id}")
    public HttpEntity<EntityModel<ProblemTypeDto>> getProblemTypeById(@PathVariable UUID id) {
        ProblemType problemType = problemTypeService.findById(id);
        return ResponseEntity.ok(problemTypeAssembler.toModel(problemType));
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @GetMapping("/{id}/" + Constants.PROBLEM_TYPE_PARENT_LIST)
    public HttpEntity<CollectionModel<EntityModel<ProblemTypeDto>>> getProblemTypeParentList(@PathVariable UUID id) {
        var entities = problemTypeService.getParentList(id);
        return ResponseEntity.ok(problemTypeAssembler.toModel(entities));
    }
}
