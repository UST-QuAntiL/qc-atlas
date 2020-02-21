package org.planqk.quality.api.controller;

import org.planqk.quality.api.Constants;
import org.planqk.quality.api.dtos.AlgorithmListDto;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Controller to access and manipulate quantum algorithms.
 */
@RestController
@RequestMapping(Constants.ALGORITHMS)
public class AlgorithmController {

    @GetMapping("/")
    public HttpEntity<RepresentationModel> getAlgorithms() {
        AlgorithmListDto dto = new AlgorithmListDto();
        // TODO: retrieve algorithm entities form backend
        dto.add(linkTo(methodOn(AlgorithmController.class).getAlgorithms()).withSelfRel());
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}
