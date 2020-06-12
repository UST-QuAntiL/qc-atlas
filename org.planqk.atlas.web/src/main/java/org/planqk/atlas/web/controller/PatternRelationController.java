package org.planqk.atlas.web.controller;

import java.util.UUID;

import javax.validation.Valid;

import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.core.services.PatternRelationService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.annotation.ApiVersion;
import org.planqk.atlas.web.dtos.PatternRelationDto;
import org.planqk.atlas.web.linkassembler.PatternRelationAssembler;
import org.planqk.atlas.web.utils.HateoasUtils;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.RestUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;

@io.swagger.v3.oas.annotations.tags.Tag(name = "pattern-relation")
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.PATTERN_RELATIONS)
@ApiVersion("v1")
@AllArgsConstructor
public class PatternRelationController {

    final private static Logger LOG = LoggerFactory.getLogger(PatternRelationController.class);

    private PatternRelationService patternRelationService;
    private PagedResourcesAssembler<PatternRelationDto> paginationAssembler;
    private PatternRelationAssembler patternRelationAssembler;

    @Operation(responses = {@ApiResponse(responseCode = "201"), @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")})
    @PostMapping("/")
    public HttpEntity<EntityModel<PatternRelationDto>> createPatternRelation(
            @Valid @RequestBody PatternRelationDto relationDto) {
        LOG.debug("Post to create new PatternRelation received.");
        // Store and return PatternRelation
        PatternRelation savedRelation = patternRelationService
                .save(ModelMapperUtils.convert(relationDto, PatternRelation.class));
        // Convert To EntityModel
        EntityModel<PatternRelationDto> dtoOutput = HateoasUtils
                .generateEntityModel(ModelMapperUtils.convert(savedRelation, PatternRelationDto.class));
        // Fill EntityModel with links
        patternRelationAssembler.addLinks(dtoOutput);

        return new ResponseEntity<>(dtoOutput, HttpStatus.CREATED);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @GetMapping("/")
    public HttpEntity<PagedModel<EntityModel<PatternRelationDto>>> getPatternRelationTypes(
            @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        LOG.debug("Get to retrieve all PatternRelations received.");
        // Generate Pageable
        Pageable p = RestUtils.getPageableFromRequestParams(page, size);
        // Get Page of DTOs
        Page<PatternRelationDto> pageDto = ModelMapperUtils.convertPage(patternRelationService.findAll(p),
                PatternRelationDto.class);
        // Generate PagedModel
        PagedModel<EntityModel<PatternRelationDto>> outputDto = paginationAssembler.toModel(pageDto);
        patternRelationAssembler.addLinks(outputDto.getContent());
        return new ResponseEntity<>(outputDto, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "404")})
    @GetMapping("/{id}")
    public HttpEntity<EntityModel<PatternRelationDto>> getPatternRelation(@PathVariable UUID id) {
        LOG.debug("Get to retrieve PatternRelation with id: {}.", id);
        PatternRelation savedPatternRelation = patternRelationService.findById(id);
        // Convert To EntityModel
        EntityModel<PatternRelationDto> dtoOutput = HateoasUtils
                .generateEntityModel(ModelMapperUtils.convert(savedPatternRelation, PatternRelationDto.class));
        // Fill EntityModel with links
        patternRelationAssembler.addLinks(dtoOutput);
        return new ResponseEntity<>(dtoOutput, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")})
    @PutMapping("/{id}")
    public HttpEntity<EntityModel<PatternRelationDto>> updatePatternRelationType(@PathVariable UUID id,
                                                                                 @Valid @RequestBody PatternRelationDto typeDto) {
        LOG.debug("Put to update PatternRelation with id: {}.", id);
        PatternRelation updatedRelation = patternRelationService.update(id,
                ModelMapperUtils.convert(typeDto, PatternRelation.class));
        // Convert To EntityModel
        EntityModel<PatternRelationDto> dtoOutput = HateoasUtils
                .generateEntityModel(ModelMapperUtils.convert(updatedRelation, PatternRelationDto.class));
        // Fill EntityModel with links
        patternRelationAssembler.addLinks(dtoOutput);
        return new ResponseEntity<>(dtoOutput, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "204")})
    @DeleteMapping("/{id}")
    public HttpEntity<?> deletePatternRelation(@PathVariable UUID id) {
        LOG.debug("Delete to remove PatternRelation with id: {}.", id);
        patternRelationService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
