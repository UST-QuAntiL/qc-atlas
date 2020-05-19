package org.planqk.atlas.web.controller;

import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.planqk.atlas.core.model.QuantumResourceType;
import org.planqk.atlas.core.services.QuantumResourceTypeService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.QuantumResourceTypeDto;
import org.planqk.atlas.web.dtos.QuantumResourceTypeListDto;
import org.planqk.atlas.web.utils.RestUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * REST controller to perform CRUD operations on the QuantumResourceTypes
 */
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.QUANTUM_RESOURCE_TYPES)
@AllArgsConstructor
public class QuantumResourceTypeController {

    private final QuantumResourceTypeService typeService;

    @GetMapping("/")
    public HttpEntity<QuantumResourceTypeListDto> getResourceTypes(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        var listDto = new QuantumResourceTypeListDto();

        var pageContents = typeService.findAll(RestUtils.getPageableFromRequestParams(page, size))
                .stream().map(e -> createResourceTypeDto(e, true)).collect(Collectors.toList());

        listDto.add(pageContents);
        listDto.add(linkTo(methodOn(QuantumResourceTypeController.class).getResourceTypes(null, null)).withSelfRel());

        return ResponseEntity.ok(listDto);
    }

    @PostMapping("/")
    public HttpEntity<QuantumResourceTypeDto> addResource(
            @RequestBody QuantumResourceTypeDto dto
    ) {
        var storedObj = this.typeService.save(QuantumResourceTypeDto.Converter.convert(dto));

        return ResponseEntity.ok(createResourceTypeDto(storedObj, true));
    }

    @GetMapping("/{id}")
    public HttpEntity<QuantumResourceTypeDto> getResource(
            @PathVariable UUID id
    ) {
        var optionalObject = typeService.findById(id);
        if (optionalObject.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var dto = createResourceTypeDto(optionalObject.get(), false);
        dto.add(
                linkTo(methodOn(QuantumResourceTypeController.class).getResource(null))
                        .withSelfRel()
        );
        return ResponseEntity.ok(dto);

    }

    private QuantumResourceTypeDto createResourceTypeDto(QuantumResourceType type, boolean addAlgoLink) {
        var dto = new QuantumResourceTypeDto();
        if (addAlgoLink) {
            dto.add(
                    linkTo(methodOn(QuantumResourceTypeController.class).getResource(null))
                            .withRel(type.getId().toString())
            );
        }
        return dto;
    }
}
