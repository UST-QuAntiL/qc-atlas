package org.planqk.atlas.web.controller;

import java.util.UUID;

import org.planqk.atlas.core.model.LearningMethod;
import org.planqk.atlas.core.services.LearningMethodService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.LearningMethodDto;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ListParametersDoc;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.ValidationGroups;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = Constants.LEARNING_METHODS)
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.LEARNING_METHODS)
@AllArgsConstructor
@Slf4j
public class LearningMethodController {

    private final LearningMethodService learningMethodService;

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
    }, description = "Retrieve all learning method")
    @ListParametersDoc
    @GetMapping
    public ResponseEntity<Page<LearningMethodDto>> getLearningMethods(
            @Parameter(hidden = true) ListParameters listParameters) {
        return ResponseEntity.ok(ModelMapperUtils
                .convertPage(learningMethodService.findAll(listParameters.getPageable(), listParameters.getSearch()), LearningMethodDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200", description = "The request has succeeded. " +
                    "The resource has been fetched and is transmitted in the message body"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404",
                         description = "Not Found. Learning method with given ID doesn't exist")
    }, description = "Retrieve a specific learning method and its basic properties.")
    @GetMapping("/{learningMethodId}")
    public ResponseEntity<LearningMethodDto> getLearningMethod(@PathVariable UUID learningMethodId) {
        final LearningMethod learningMethod = learningMethodService.findById(learningMethodId);
        return ResponseEntity.ok(ModelMapperUtils.convert(learningMethod, LearningMethodDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body."),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
    }, description = "Define the basic properties of a learning method.")
    @PostMapping
    public ResponseEntity<LearningMethodDto> createLearningMethod(
            @Validated(ValidationGroups.Create.class) @RequestBody LearningMethodDto learningMethodDto) {
        final LearningMethod savedLearningMethod = learningMethodService.create(
                ModelMapperUtils.convert(learningMethodDto, LearningMethod.class));
        return new ResponseEntity<>(ModelMapperUtils.convert(savedLearningMethod, LearningMethodDto.class), HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200", description = "The request has succeeded. " +
                    "The resource has been fetched and is transmitted in the message body"),
            @ApiResponse(responseCode = "400", description = "Bad Request. Invalid request body."),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404",
                         description = "Not Found. Learning method with given ID does not exist"),
    }, description = "Update the basic properties of an learning method (e.g. name).")
    @PutMapping("/{learningMethodId}")
    public ResponseEntity<LearningMethodDto> updateLearningMethod(
            @PathVariable UUID learningMethodId,
            @Validated(ValidationGroups.Update.class) @RequestBody LearningMethodDto learningMethodDto) {
        learningMethodDto.setId(learningMethodId);
        final LearningMethod updatedLearningMethod = learningMethodService.update(
                ModelMapperUtils.convert(learningMethodDto, LearningMethod.class));
        return ResponseEntity.ok(ModelMapperUtils.convert(updatedLearningMethod, LearningMethodDto.class));
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "204", description = "There is no content to send for this request."),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not Found. Learning method with given ID doesn't exist")
    }, description = "Delete a learning method. This removes the learning method from all algorithms it is references in.")
    @DeleteMapping("/{learningMethodId}")
    public ResponseEntity<Void> deleteLearningMethod(@PathVariable UUID learningMethodId) {
        learningMethodService.delete(learningMethodId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
