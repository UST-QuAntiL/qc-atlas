package org.planqk.atlas.web.controller;

import javax.validation.Valid;

import org.planqk.atlas.core.model.PatternRelation;
import org.planqk.atlas.core.model.PatternRelationType;
import org.planqk.atlas.core.services.PatternRelationService;
import org.planqk.atlas.core.services.PatternRelationTypeService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.annotation.ApiVersion;
import org.planqk.atlas.web.dtos.PatternRelationDto;
import org.planqk.atlas.web.dtos.PatternRelationTypeDto;
import org.planqk.atlas.web.linkassembler.PatternRelationAssembler;
import org.planqk.atlas.web.linkassembler.PatternRelationTypeAssembler;
import org.planqk.atlas.web.utils.HateoasUtils;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @Operation(responses = { @ApiResponse(responseCode = "201"), @ApiResponse(responseCode = "404") })
    @PostMapping("/")
    public HttpEntity<EntityModel<PatternRelationDto>> createPatternRelation(
            @Valid @RequestBody PatternRelationDto relationDto) {
        LOG.debug("Post to create new PatternRelation received.");
        
        PatternRelation relation = ModelMapperUtils.convert(relationDto, PatternRelation.class);
        LOG.info("AlgoId: " + relation.getAlgorithm().getId());
        LOG.info("TypeId: " + relation.getPatternRelationType().getId().toString());
//        // Store and return PatternRelation
//        PatternRelation savedRelation = patternRelationService
//                .save(ModelMapperUtils.convert(relationDto, PatternRelation.class));
//        // Convert To EntityModel
//        EntityModel<PatternRelationDto> dtoOutput = HateoasUtils
//                .generateEntityModel(ModelMapperUtils.convert(savedRelation, PatternRelationDto.class));
//        // Fill EntityModel with links
//        patternRelationAssembler.addLinks(dtoOutput);
        
        
        return new ResponseEntity<>(HateoasUtils.generateEntityModel(relationDto), HttpStatus.CREATED);
//        return new ResponseEntity<>(dtoOutput, HttpStatus.CREATED);
    }

}
