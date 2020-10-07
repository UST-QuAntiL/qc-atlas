package org.planqk.atlas.web.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.MalformedURLException;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.core.services.ImplementationService;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.dtos.ImplementationDto;
import org.planqk.atlas.web.linkassembler.EnableLinkAssemblers;
import org.planqk.atlas.web.linkassembler.LinkBuilderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = ImplementationGlobalController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@EnableLinkAssemblers
public class ImplementationGlobalControllerTest {

    private final ObjectMapper mapper = ObjectMapperUtils.newTestMapper();

    @MockBean
    private AlgorithmService algorithmService;

    @MockBean
    private ImplementationService implementationService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LinkBuilderService linkBuilderService;

    private Implementation implementation1;

    @Test
    public void getImplementation() throws Exception {
        this.implementation1 = mockValidMinimalImpl(UUID.randomUUID());

        when(implementationService.findById(this.implementation1.getId())).thenReturn(this.implementation1);

        var url = linkBuilderService.urlStringTo(methodOn(ImplementationGlobalController.class)
                .getImplementation(implementation1.getId()));

        MvcResult result = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        EntityModel<ImplementationDto> response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertEquals(response.getContent().getId(), implementation1.getId());
    }

    private Implementation mockValidMinimalImpl(UUID implId) throws MalformedURLException {
        Algorithm algo = new Algorithm();
        algo.setName("dummy");
        algorithmService.create(algo);

        Implementation implementation = new Implementation();
        implementation.setName("implementation for Shor");
        implementation.setId(implId);
        when(implementationService.create(any(Implementation.class), any())).thenReturn(implementation);
        return implementation;
    }

}
