package org.planqk.atlas.web.controller;

import java.util.Objects;
import java.util.UUID;

import javax.validation.Valid;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.core.model.PatternRelationType;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.core.services.PatternRelationService;
import org.planqk.atlas.core.services.PatternRelationTypeService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.PatternRelationDto;
import org.planqk.atlas.web.linkassembler.PatternRelationAssembler;
import org.planqk.atlas.web.utils.RestUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@io.swagger.v3.oas.annotations.tags.Tag(name = "pattern-relation")
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.API_VERSION + "/" + Constants.PATTERN_RELATIONS)
@AllArgsConstructor
@Slf4j
public class PatternRelationController {

    private AlgorithmService algorithmService;
    private PatternRelationTypeService patternRelationTypeService;
    private PatternRelationService patternRelationService;
    private PatternRelationAssembler patternRelationAssembler;

    @Operation(responses = {@ApiResponse(responseCode = "201"), @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")}, description = "Add a pattern relation from an algorithm to a given pattern. Custom ID will be ignored. For pattern relation type only ID is required, other pattern relation type attributes will not change.")
    @PostMapping()
    public HttpEntity<EntityModel<PatternRelationDto>> createPatternRelation(
            @Valid @RequestBody PatternRelationDto relationDto) {
        log.debug("Post to create new PatternRelation received.");
        return new ResponseEntity<>(handlePatternRelationUpdate(relationDto, null), HttpStatus.CREATED);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @GetMapping()
    public HttpEntity<PagedModel<EntityModel<PatternRelationDto>>> getPatternRelationTypes(
            @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        log.debug("Get to retrieve all PatternRelations received.");
        Pageable p = RestUtils.getPageableFromRequestParams(page, size);
        var entities = patternRelationService.findAll(p);
        return ResponseEntity.ok(patternRelationAssembler.toModel(entities));
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "400"), @ApiResponse(responseCode = "404")})
    @GetMapping("/{id}")
    public HttpEntity<EntityModel<PatternRelationDto>> getPatternRelation(@PathVariable UUID id) {
        log.debug("Get to retrieve PatternRelation with id: {}.", id);
        var patternRelation = patternRelationService.findById(id);
        return ResponseEntity.ok(patternRelationAssembler.toModel(patternRelation));
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")},
            description = "Update a reference to a pattern. Custom ID will be ignored. For pattern relation type only ID is required, other pattern relation type attributes will not change.")
    @PutMapping("/{id}")
    public HttpEntity<EntityModel<PatternRelationDto>> updatePatternRelationType(@PathVariable UUID id,
                                                                                 @Valid @RequestBody PatternRelationDto relationDto) {
        log.debug("Put to update PatternRelation with id: {}.", id);
        return ResponseEntity.ok(handlePatternRelationUpdate(relationDto, id));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404", description = "Pattern relation with given id doesn't exist")})
    @DeleteMapping("/{id}")
    public HttpEntity<Void> deletePatternRelation(@PathVariable UUID id) {
        log.debug("Delete to remove PatternRelation with id: {}.", id);
        patternRelationService.findById(id);
        patternRelationService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private EntityModel<PatternRelationDto> handlePatternRelationUpdate(PatternRelationDto relationDto, UUID relationId) {
        PatternRelation patternRelation = new PatternRelation();
        if (Objects.nonNull(relationId)) {
            patternRelation.setId(relationId);
        }

        // Convert Dto to PatternRelation by using content from the database
        Algorithm algorithm = algorithmService.findById(relationDto.getAlgorithm().getId());
        PatternRelationType patternRelationType = patternRelationTypeService.findById(relationDto.getPatternRelationType().getId());
        patternRelation.setAlgorithm(algorithm);
        patternRelation.setPattern(relationDto.getPattern());
        patternRelation.setDescription(relationDto.getDescription());
        patternRelation.setPatternRelationType(patternRelationType);

        // Store and return PatternRelation
        PatternRelation savedRelation = patternRelationService.save(patternRelation);
        return patternRelationAssembler.toModel(savedRelation);
    }
}
