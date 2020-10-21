/*******************************************************************************
 * Copyright (c) 2020 the qc-atlas contributors.
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.planqk.atlas.web.controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.Implementation;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.services.AlgorithmService;
import org.planqk.atlas.core.services.ImplementationService;
import org.planqk.atlas.core.services.LinkingService;
import org.planqk.atlas.core.services.PublicationService;
import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
import org.planqk.atlas.web.dtos.AlgorithmDto;
import org.planqk.atlas.web.dtos.PublicationDto;
import org.planqk.atlas.web.linkassembler.EnableLinkAssemblers;
import org.planqk.atlas.web.linkassembler.LinkBuilderService;
import org.planqk.atlas.web.utils.ListParameters;
import org.planqk.atlas.web.utils.ModelMapperUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = PublicationController.class)
@ExtendWith({MockitoExtension.class})
@AutoConfigureMockMvc
@EnableLinkAssemblers
public class PublicationControllerTest {

    @MockBean
    private PublicationService publicationService;
    @MockBean
    private AlgorithmService algorithmService;
    @MockBean
    private ImplementationService implementationService;
    @MockBean
    private LinkingService linkingService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private LinkBuilderService linkBuilderService;

    private final ObjectMapper mapper = ObjectMapperUtils.newTestMapper();

    @Test
    @SneakyThrows
    void getPublications_EmptyList_returnOk() {
        doReturn(new PageImpl<Publication>(List.of())).when(publicationService).findAll(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .getPublications(ListParameters.getDefault()));
         mockMvc
                .perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.publications").doesNotExist());
    }

    @Test
    @SneakyThrows
    void getPublications_SingleElement_returnOk() {
        var publ = new Publication();
        publ.setId(UUID.randomUUID());
        publ.setAuthors(List.of("test", "test-2"));
        publ.setTitle("test");

        doReturn(new PageImpl<Publication>(List.of(publ))).when(publicationService).findAll(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .getPublications(ListParameters.getDefault()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.publications[0].id").value(publ.getId().toString()))
                .andExpect(jsonPath("$._embedded.publications[0].title").value(publ.getTitle()))
                .andExpect(jsonPath("$._embedded.publications[0].authors").isArray());
    }

    @Test
    @SneakyThrows
    void getPublication_returnOk() {
        var publ = new Publication();
        publ.setId(UUID.randomUUID());
        publ.setAuthors(List.of("test", "test-2"));
        publ.setTitle("test");

        doReturn(publ).when(publicationService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .getPublication(publ.getId()));

        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(publ.getId().toString()))
                .andExpect(jsonPath("$.title").value(publ.getTitle()))
                .andExpect(jsonPath("$.authors").isArray());
    }

    @Test
    @SneakyThrows
    void getPublication_returnNotFound() {
        doThrow(new NoSuchElementException()).when(publicationService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .getPublication(UUID.randomUUID()));
        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void createPublication_returnCreated() {
        var publ = new Publication();
        publ.setAuthors(List.of("test", "test-2"));
        publ.setTitle("test");
        var publDto = ModelMapperUtils.convert(publ, PublicationDto.class);
        publ.setId(UUID.randomUUID());
        doReturn(publ).when(publicationService).create(any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .createPublication(null));
        mockMvc.perform(
                post(url)
                        .accept(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(publDto))
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(publ.getId().toString()));
    }

    @Test
    @SneakyThrows
    void createPublication_returnBadRequest() {
        var publ = new Publication();
        publ.setAuthors(List.of());
        publ.setTitle("test");
        var publDto = ModelMapperUtils.convert(publ, PublicationDto.class);
        publ.setId(UUID.randomUUID());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .createPublication(null));
        mockMvc.perform(
                post(url)
                        .accept(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(publDto))
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void updatePublication_returnOk() {
        var publ = new Publication();
        publ.setAuthors(List.of("test", "test-2"));
        publ.setTitle("test");
        publ.setId(UUID.randomUUID());
        var publDto = ModelMapperUtils.convert(publ, PublicationDto.class);
        doReturn(publ).when(publicationService).update(any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .updatePublication(publ.getId(), null));
        mockMvc.perform(
                put(url)
                        .accept(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(publDto))
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(publ.getId().toString()));
    }

    @Test
    @SneakyThrows
    void updatePublication_returnBadRequest() {
        var publ = new Publication();
        publ.setAuthors(List.of());
        publ.setTitle("test");
        publ.setId(UUID.randomUUID());
        var publDto = ModelMapperUtils.convert(publ, PublicationDto.class);

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .updatePublication(publ.getId(), null));
        mockMvc.perform(
                put(url)
                        .accept(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(publDto))
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void updatePublication_returnNotFound() {
        var publ = new Publication();
        publ.setAuthors(List.of("test", "test-2"));
        publ.setTitle("test");
        publ.setId(UUID.randomUUID());
        var publDto = ModelMapperUtils.convert(publ, PublicationDto.class);
        doThrow(new NoSuchElementException()).when(publicationService).update(any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .updatePublication(publ.getId(), null));
        mockMvc.perform(
                put(url)
                        .accept(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(publDto))
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void deletePublication_returnNoContent() {
        doNothing().when(publicationService).delete(any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .deletePublication(UUID.randomUUID()));

        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void deletePublication_returnNotFound() {
        doThrow(new NoSuchElementException()).when(publicationService).delete(any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .deletePublication(UUID.randomUUID()));

        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getAlgorithmsOfPublication_SingleElement_returnOk() {
        var algo = new Algorithm();
        algo.setName("algo");
        algo.setId(UUID.randomUUID());

        doReturn(new PageImpl<>(List.of(algo))).when(publicationService).findLinkedAlgorithms(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .getAlgorithmsOfPublication(UUID.randomUUID(), ListParameters.getDefault()));

        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(jsonPath("$._embedded.algorithms[0].id").value(algo.getId().toString()))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getAlgorithmsOfPublication_EmptyList_returnOk() {
        doReturn(new PageImpl<>(List.of())).when(publicationService).findLinkedAlgorithms(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .getAlgorithmsOfPublication(UUID.randomUUID(), ListParameters.getDefault()));

        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(jsonPath("$._embedded.algorithms").doesNotExist())
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getAlgorithmsOfPublication_returnNotFound() {
        doThrow(new NoSuchElementException()).when(publicationService).checkIfAlgorithmIsLinkedToPublication(any(), any());
        doThrow(new NoSuchElementException()).when(publicationService).findLinkedAlgorithms(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .getAlgorithmsOfPublication(UUID.randomUUID(), ListParameters.getDefault()));

        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getAlgorithmOfPublication_returnOk() {
        var algo = new Algorithm();
        algo.setName("algo");
        algo.setId(UUID.randomUUID());

        doNothing().when(publicationService).checkIfAlgorithmIsLinkedToPublication(any(), any());
        doReturn(algo).when(algorithmService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .getAlgorithmOfPublication(UUID.randomUUID(), UUID.randomUUID()));

        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(algo.getId().toString()))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getAlgorithmOfPublication_returnNotFound() {
        doThrow(new NoSuchElementException()).when(publicationService).checkIfAlgorithmIsLinkedToPublication(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .getAlgorithmOfPublication(UUID.randomUUID(), UUID.randomUUID()));

        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void linkPublicationAndAlgorithm_returnNoContent() {
        doNothing().when(linkingService).linkAlgorithmAndPublication(any(), any());
        ;
        var algoDto = new AlgorithmDto();
        algoDto.setId(UUID.randomUUID());
        algoDto.setComputationModel(ComputationModel.QUANTUM);

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .linkPublicationAndAlgorithm(UUID.randomUUID(), null));

        mockMvc.perform(
                post(url)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(algoDto))
        ).andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void linkPublicationAndAlgorithm_returnBadRequest() {
        var algoDto = new AlgorithmDto();
        algoDto.setId(null);
        algoDto.setComputationModel(ComputationModel.QUANTUM);

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .linkPublicationAndAlgorithm(UUID.randomUUID(), null));

        mockMvc.perform(
                post(url)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(algoDto))
        ).andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void linkPublicationAndAlgorithm_returnNotFound() {
        doThrow(new NoSuchElementException()).when(linkingService).linkAlgorithmAndPublication(any(), any());
        var algoDto = new AlgorithmDto();
        algoDto.setId(UUID.randomUUID());
        algoDto.setComputationModel(ComputationModel.QUANTUM);

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .linkPublicationAndAlgorithm(UUID.randomUUID(), null));

        mockMvc.perform(
                post(url)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(algoDto))
        ).andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void unlinkPublicationAndAlgorithm_returnNoContent() {
        doNothing().when(linkingService).unlinkImplementationAndPublication(any(), any());
        ;

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .unlinkPublicationAndAlgorithm(UUID.randomUUID(), UUID.randomUUID()));

        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void unlinkPublicationAndAlgorithm_returnNotFound() {
        doThrow(new NoSuchElementException()).when(linkingService).unlinkAlgorithmAndPublication(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .unlinkPublicationAndAlgorithm(UUID.randomUUID(), UUID.randomUUID()));

        mockMvc.perform(delete(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getImplementationsOfPublication_EmptyList_returnOk() {
        doReturn(new PageImpl<>(List.of())).when(publicationService).findLinkedImplementations(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .getImplementationsOfPublication(UUID.randomUUID(), ListParameters.getDefault()));

        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(jsonPath("$._embedded.implementations").doesNotExist())
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getImplementationsOfPublication_returnNotFound() {
        doThrow(new NoSuchElementException()).when(publicationService).checkIfImplementationIsLinkedToPublication(any(), any());
        doThrow(new NoSuchElementException()).when(publicationService).findLinkedImplementations(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .getImplementationsOfPublication(UUID.randomUUID(), ListParameters.getDefault()));

        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getImplementationOfPublication_returnOk() {
        var algo = new Implementation();
        algo.setName("algo");
        algo.setId(UUID.randomUUID());

        doNothing().when(publicationService).checkIfImplementationIsLinkedToPublication(any(), any());
        doReturn(algo).when(implementationService).findById(any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .getImplementationOfPublication(UUID.randomUUID(), UUID.randomUUID()));

        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(algo.getId().toString()))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getImplementationOfPublication_returnNotFound() {
        doThrow(new NoSuchElementException()).when(publicationService).checkIfImplementationIsLinkedToPublication(any(), any());

        var url = linkBuilderService.urlStringTo(methodOn(PublicationController.class)
                .getImplementationOfPublication(UUID.randomUUID(), UUID.randomUUID()));

        mockMvc.perform(get(url).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
