package org.planqk.atlas.web.controller;


import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.services.PublicationService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.*;
import org.planqk.atlas.web.utils.RestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Controller to access and manipulate publication algorithms.
 */
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.PUBLICATIONS)
public class PublicationController {

    final private static Logger LOGGER = LoggerFactory.getLogger(PublicationController.class);

    private PublicationService publicationService;

    public PublicationController(PublicationService problemTypeService) {
        this.publicationService = problemTypeService;
    }

    public static PublicationListDto createPublicationDtoList(Page<Publication> publications){
        PublicationListDto publicationListDto = new PublicationListDto();

        for (Publication pub :  publications){
            publicationListDto.add(createPublicationDto(pub));
            publicationListDto.add(linkTo(methodOn(PublicationController.class).getPublication(pub.getId())).withRel(pub.getId().toString()));
        }

        return publicationListDto;
    }

    public static PublicationDto createPublicationDto(Publication publication) {

        PublicationDto publicationDto = PublicationDto.Converter.convert(publication);

        // Add Self-Reference
        publicationDto.add(linkTo(methodOn(PublicationController.class).getPublication(publication.getId())).withSelfRel());

        return publicationDto;
    }

    @GetMapping("/")
    public HttpEntity<PublicationListDto> getPublications(@RequestParam(required = false) Integer page,
                                                   @RequestParam(required = false) Integer size) {

        PublicationListDto publicationListDto = createPublicationDtoList(publicationService.findAll(RestUtils.getPageableFromRequestParams(page,size)));

        publicationListDto.add(linkTo(methodOn(PublicationController.class).getPublications(null,null)).withSelfRel());
        return new ResponseEntity<>(publicationListDto,HttpStatus.OK);
    }

    @PostMapping("/")
    public HttpEntity<PublicationDto> createPublication(@RequestBody PublicationDto publicationDto) {

        Publication publication = publicationService.save(PublicationDto.Converter.convert(publicationDto));

        if(Objects.isNull(publicationDto.getTitle())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(createPublicationDto(publication), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public HttpEntity<PublicationDto> getPublication(@PathVariable UUID id) {
        Optional<Publication> publicationOpt = publicationService.findById(id);

        if( !publicationOpt.isPresent()){
            LOGGER.error("Could not retrieve publication with id {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        PublicationDto publicationDto = createPublicationDto(publicationOpt.get());
        return new ResponseEntity<>(publicationDto, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public HttpEntity<PublicationDto> updatePublication(@PathVariable UUID id, @RequestBody PublicationDto pub) {
        LOGGER.debug("Put to update algorithm with id '" + id + "' received");

        if (Objects.isNull(pub.getTitle())) {
            LOGGER.error("Received invalid publication object for post request: {}", pub.toString());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Publication publication = publicationService.update(id, PublicationDto.Converter.convert(pub));
        return new ResponseEntity<>(createPublicationDto(publication), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public HttpEntity<AlgorithmDto> deletePublication(@PathVariable UUID id) {
        LOGGER.debug("Delete to remove algorithm with id '" + id + "' received");

        if (publicationService.findById(id).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        publicationService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{id}/" + Constants.ALGORITHMS)
    public HttpEntity<AlgorithmListDto> getAlgorithms(@PathVariable UUID id) {
        Optional<Publication> algorithmOptional = publicationService.findById(id);
        if (!algorithmOptional.isPresent()) {
            LOGGER.error("Unable to retrieve algorithm with id {} form the repository.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        //TODO
        return null;
    }
}





