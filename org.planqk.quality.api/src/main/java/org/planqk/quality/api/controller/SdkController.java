package org.planqk.quality.api.controller;

import org.planqk.quality.api.Constants;
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
 * Controller to access and manipulate software development kits (SDKs).
 */
@RestController
@RequestMapping(Constants.SDKS)
public class SdkController {

    @GetMapping("/")
    public HttpEntity<RepresentationModel> getSdks() {
        // TODO: display all existing qpu entities
        RepresentationModel responseEntity = new RepresentationModel<>();
        responseEntity.add(linkTo(methodOn(SdkController.class).getSdks()).withSelfRel());
        return new ResponseEntity<>(responseEntity, HttpStatus.OK);
    }
}
