package org.planqk.atlas.web.controller;
;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.services.PublicationService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.annotation.ApiVersion;
import org.planqk.atlas.web.dtos.*;
import org.planqk.atlas.web.linkassembler.AlgorithmAssembler;
import org.planqk.atlas.web.linkassembler.PublicationAssembler;
import org.planqk.atlas.web.utils.HateoasUtils;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.planqk.atlas.web.utils.RestUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

/**
 * Controller to access and manipulate publication algorithms.
 */
@Slf4j
@RestController
@ApiVersion("v1")
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.PUBLICATIONS)
public class PublicationController {

    private PublicationService publicationService;
    private PublicationAssembler publicationAssembler;
    private AlgorithmAssembler algorithmAssembler;
    private PagedResourcesAssembler<PublicationDto> paginationAssembler;

    @GetMapping("/")
    public HttpEntity<PagedModel<EntityModel<PublicationDto>>> getPublications(@RequestParam(required = false) Integer page,
                                                   @RequestParam(required = false) Integer size) {
        log.debug("Get all publications");
        Pageable pageable = RestUtils.getPageableFromRequestParams(page,size);
        Page<PublicationDto> dtoPage = ModelMapperUtils.convertPage(publicationService.findAll(pageable), PublicationDto.class);
        PagedModel<EntityModel<PublicationDto>> outputModel = paginationAssembler.toModel(dtoPage);
        publicationAssembler.addLinks(outputModel.getContent());
        return new ResponseEntity<>(outputModel,HttpStatus.OK);
    }

    @PostMapping("/")
    public HttpEntity<EntityModel<PublicationDto>> createPublication(@Validated @RequestBody PublicationDto publicationDto) {
        log.debug("Create publication");
        Publication publication = publicationService.save(ModelMapperUtils.convert(publicationDto, Publication.class));
        EntityModel<PublicationDto> dtoEntityModel = HateoasUtils.generateEntityModel(ModelMapperUtils.convert(publication, PublicationDto.class));
        publicationAssembler.addLinks(dtoEntityModel);
        return new ResponseEntity<>(dtoEntityModel, HttpStatus.CREATED);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", content = @Content)
    })
    @GetMapping("/{id}")
    public HttpEntity<EntityModel<PublicationDto>> getPublication(@PathVariable UUID id) {
        log.debug("Get publication with id: {}",id);
        Publication publication = publicationService.findById(id);
        EntityModel<PublicationDto> dtoEntityModel = HateoasUtils.generateEntityModel(ModelMapperUtils.convert(publication, PublicationDto.class));
        publicationAssembler.addLinks(dtoEntityModel);
        return new ResponseEntity<>(dtoEntityModel, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public HttpEntity<EntityModel<PublicationDto>> updatePublication(@PathVariable UUID id, @Validated @RequestBody PublicationDto pub) {
        log.debug("Put to update algorithm with id '" + id + "' received");
        Publication publication = publicationService.update(id,ModelMapperUtils.convert(pub, Publication.class));
        EntityModel<PublicationDto> dtoEntityModel = HateoasUtils.generateEntityModel(ModelMapperUtils.convert(publication,PublicationDto.class));
        publicationAssembler.addLinks(dtoEntityModel);
        return new ResponseEntity<>(dtoEntityModel, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public HttpEntity<AlgorithmDto> deletePublication(@PathVariable UUID id) {
        log.debug("Delete to remove algorithm with id '" + id + "' received");
        publicationService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{id}/" + Constants.ALGORITHMS)
    public HttpEntity<CollectionModel<EntityModel<AlgorithmDto>>> getAlgorithms(@PathVariable UUID id) {
        log.debug("Get algorithms of Publication with id {}",id);
        Publication publication = publicationService.findById(id);
        Set<Algorithm> algorithms = publication.getAlgorithms();
        Set<AlgorithmDto> algorithmDtos = ModelMapperUtils.convertSet(algorithms, AlgorithmDto.class);
        CollectionModel<EntityModel<AlgorithmDto>> resultCollection = HateoasUtils.generateCollectionModel(algorithmDtos);
        algorithmAssembler.addLinks(resultCollection);
        publicationAssembler.addAlgorithmLink(resultCollection,id);
        return new ResponseEntity<>(resultCollection,HttpStatus.OK);
    }
}





