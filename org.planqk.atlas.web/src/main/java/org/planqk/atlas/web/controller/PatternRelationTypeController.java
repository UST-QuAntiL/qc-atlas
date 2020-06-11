package org.planqk.atlas.web.controller;

import java.util.UUID;

import javax.validation.Valid;

import org.planqk.atlas.core.model.PatternRelationType;
import org.planqk.atlas.core.services.PatternRelationTypeService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.annotation.ApiVersion;
import org.planqk.atlas.web.dtos.PatternRelationTypeDto;
import org.planqk.atlas.web.linkassembler.PatternRelationTypeAssembler;
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

@io.swagger.v3.oas.annotations.tags.Tag(name = "pattern-relation-type")
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.PATTERN_RELATION_TYPES)
@ApiVersion("v1")
@AllArgsConstructor
public class PatternRelationTypeController {

    final private static Logger LOG = LoggerFactory.getLogger(PatternRelationTypeController.class);

    private PatternRelationTypeService patternRelationTypeService;
    private PagedResourcesAssembler<PatternRelationTypeDto> paginationAssembler;
    private PatternRelationTypeAssembler patternRelationTypeAssembler;

    @Operation(responses = { @ApiResponse(responseCode = "201"), @ApiResponse(responseCode = "400") })
    @PostMapping("/")
    public HttpEntity<EntityModel<PatternRelationTypeDto>> createPatternRelationType(
            @Valid @RequestBody PatternRelationTypeDto typeDto) {
        LOG.debug("Post to create new PatternRelationTypes received.");
        // Store and return PatternRelationType
        PatternRelationType savedRelationType = patternRelationTypeService
                .save(ModelMapperUtils.convert(typeDto, PatternRelationType.class));
        // Convert To EntityModel
        EntityModel<PatternRelationTypeDto> dtoOutput = HateoasUtils
                .generateEntityModel(ModelMapperUtils.convert(savedRelationType, PatternRelationTypeDto.class));
        // Fill EntityModel with links
        patternRelationTypeAssembler.addLinks(dtoOutput);
        return new ResponseEntity<>(dtoOutput, HttpStatus.CREATED);
    }

    @Operation(responses = { @ApiResponse(responseCode = "200") })
    @GetMapping("/")
    public HttpEntity<PagedModel<EntityModel<PatternRelationTypeDto>>> getPatternRelationTypes(
            @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        LOG.debug("Get to retrieve all PatternRelationTypes received.");
        // Generate Pageable
        Pageable p = RestUtils.getPageableFromRequestParams(page, size);
        // Get Page of DTOs
        Page<PatternRelationTypeDto> pageDto = ModelMapperUtils.convertPage(patternRelationTypeService.findAll(p),
                PatternRelationTypeDto.class);
        // Generate PagedModel
        PagedModel<EntityModel<PatternRelationTypeDto>> outputDto = paginationAssembler.toModel(pageDto);
        patternRelationTypeAssembler.addLinks(outputDto.getContent());
        return new ResponseEntity<>(outputDto, HttpStatus.OK);
    }

    @Operation(responses = { @ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "404") })
    @GetMapping("/{id}")
    public HttpEntity<EntityModel<PatternRelationTypeDto>> getPatternRelationType(@PathVariable UUID id) {
        LOG.debug("Get to retrieve PatternRelationType with id: {}.", id);
        PatternRelationType savedRelationType = patternRelationTypeService.findById(id);
        // Convert To EntityModel
        EntityModel<PatternRelationTypeDto> dtoOutput = HateoasUtils
                .generateEntityModel(ModelMapperUtils.convert(savedRelationType, PatternRelationTypeDto.class));
        // Fill EntityModel with links
        patternRelationTypeAssembler.addLinks(dtoOutput);
        return new ResponseEntity<>(dtoOutput, HttpStatus.OK);
    }

    @Operation(responses = { @ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404") })
    @PutMapping("/{id}")
    public HttpEntity<EntityModel<PatternRelationTypeDto>> updatePatternRelationType(@PathVariable UUID id,
            @Valid @RequestBody PatternRelationTypeDto typeDto) {
        LOG.debug("Put to update PatternRelationType with id: {}.", id);
        PatternRelationType updatedRelationType = patternRelationTypeService.update(id,
                ModelMapperUtils.convert(typeDto, PatternRelationType.class));
        // Convert To EntityModel
        EntityModel<PatternRelationTypeDto> dtoOutput = HateoasUtils
                .generateEntityModel(ModelMapperUtils.convert(updatedRelationType, PatternRelationTypeDto.class));
        // Fill EntityModel with links
        patternRelationTypeAssembler.addLinks(dtoOutput);
        return new ResponseEntity<>(dtoOutput, HttpStatus.OK);
    }

    @Operation(responses = { @ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "204") })
    @DeleteMapping("/{id}")
    public HttpEntity<?> deletePatternRelationType(@PathVariable UUID id) {
        LOG.debug("Delete to remove PatternRelationType with id: {}.", id);
        patternRelationTypeService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
