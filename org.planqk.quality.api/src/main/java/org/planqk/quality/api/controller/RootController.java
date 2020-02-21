package org.planqk.quality.api.controller;

import org.planqk.quality.api.Constants;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

/**
 * Root controller to access all entities within Quality, trigger the hardware selection, and execution of quantum algorithms.
 */
@RestController
public class RootController {

    @GetMapping("/")
    public HttpEntity<RepresentationModel> root() {
        RepresentationModel responseEntity = new RepresentationModel<>();

        // add links to sub-controllers
        responseEntity.add(linkTo(methodOn(RootController.class).root()).withSelfRel());
        responseEntity.add(linkTo(methodOn(AlgorithmController.class).getAlgorithms()).withRel(Constants.ALGORITHMS));
        responseEntity.add(linkTo(methodOn(ImplementationController.class).getImplementations()).withRel(Constants.IMPLEMENTATIONS));
        responseEntity.add(linkTo(methodOn(ProviderController.class).getProviders()).withRel(Constants.PROVIDERS));
        responseEntity.add(linkTo(methodOn(QpuController.class).getQpus()).withRel(Constants.QPUS));
        responseEntity.add(linkTo(methodOn(SdkController.class).getSdks()).withRel(Constants.SDKS));

        return new ResponseEntity<>(responseEntity, HttpStatus.OK);
    }
}
