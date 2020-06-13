package org.planqk.atlas.web.controller;

import java.util.Optional;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.planqk.atlas.core.model.Backend;
import org.planqk.atlas.core.services.BackendService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.annotation.ApiVersion;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.BackendDto;
import org.planqk.atlas.web.linkassembler.BackendAssembler;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.BACKENDS)
@ApiVersion("v1")
@AllArgsConstructor
public class BackendController {

    final private static Logger LOG = LoggerFactory.getLogger(BackendController.class);

    private BackendService backendService;
    private BackendAssembler backendAssembler;
    private PagedResourcesAssembler<BackendDto> paginationAssembler;

    @Operation(responses = {@ApiResponse(responseCode = "200")})
    @GetMapping("/")
    public HttpEntity<PagedModel<EntityModel<BackendDto>>> getBackends(@RequestParam(required = false) Integer page,
                                                                         @RequestParam(required = false) Integer size) {
        LOG.debug("Get to retrieve all Backends received");
        Pageable p = RestUtils.getPageableFromRequestParams(page, size);
        Page<BackendDto> pageDto = ModelMapperUtils.convertPage(backendService.findAll(p), BackendDto.class);
        PagedModel<EntityModel<BackendDto>> outputDto = paginationAssembler.toModel(pageDto);
        return new ResponseEntity<>(outputDto, HttpStatus.OK);
    }

    @Operation(responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "404")})
    @GetMapping("/{id}")
    public HttpEntity<EntityModel<BackendDto>> getBackend(@PathVariable UUID id) {
        LOG.debug("Get to retrieve Backend with id " + id.toString() + " received");
        Optional<Backend> backend = backendService.findOptionalById(id);

        if (backend.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
            EntityModel<BackendDto> dtoOutput = HateoasUtils.generateEntityModel(ModelMapperUtils.convert(backend.get(), BackendDto.class));
            backendAssembler.addLinks(dtoOutput);
            return new ResponseEntity<>(dtoOutput, HttpStatus.OK);

        }
    }
}
