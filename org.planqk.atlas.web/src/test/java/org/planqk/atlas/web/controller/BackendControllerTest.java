//package org.planqk.atlas.web.controller;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Comparator;
//import java.util.HashSet;
//import java.util.List;
//import java.util.NoSuchElementException;
//import java.util.Set;
//import java.util.UUID;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentMatchers;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.planqk.atlas.core.model.Backend;
//import org.planqk.atlas.core.model.BackendProperty;
//import org.planqk.atlas.core.model.BackendPropertyType;
//import org.planqk.atlas.core.model.Qpu;
//import org.planqk.atlas.core.model.QuantumComputationModel;
//import org.planqk.atlas.core.model.Simulator;
//import org.planqk.atlas.core.services.BackendService;
//import org.planqk.atlas.web.Constants;
//import org.planqk.atlas.web.controller.util.ObjectMapperUtils;
//import org.planqk.atlas.web.dtos.BackendDto;
//import org.planqk.atlas.web.linkassembler.BackendAssembler;
//import org.planqk.atlas.web.utils.HateoasUtils;
//import org.planqk.atlas.web.utils.ModelMapperUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.web.PagedResourcesAssembler;
//import org.springframework.hateoas.EntityModel;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.doThrow;
//import static org.mockito.Mockito.when;
//import static org.junit.Assert.assertEquals;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(BackendController.class)
//@ExtendWith(MockitoExtension.class)
//@AutoConfigureMockMvc
//public class BackendControllerTest {
//
//    @MockBean
//    private BackendService service;
//    @MockBean
//    private PagedResourcesAssembler<BackendDto> paginationAssembler;
//    @MockBean
//    private BackendAssembler backendAssembler;
//
//    @Autowired
//    private MockMvc mockMvc;
//    private ObjectMapper mapper;
//
//    private final int page = 0;
//    private final int size = 2;
//    private final Pageable pageable = PageRequest.of(page, size);
//
//    private Backend backend1;
//    private BackendDto backendDto1;
//    private List<Backend> backendList;
//    private Set<BackendProperty> properties;
//    private QuantumComputationModel model;
//
//    @BeforeEach
//    public void init() {
//        mapper = ObjectMapperUtils.newTestMapper();
//        backendList = new ArrayList<>();
//        properties = new HashSet<>();
//
//        BackendPropertyType propertyType = new BackendPropertyType();
//        propertyType.setDescription("desc");
//        propertyType.setName("name");
//        propertyType.setId(UUID.randomUUID());
//
//        BackendProperty property = new BackendProperty();
//        property.setType(propertyType);
//        property.setValue("some value");
//        property.setId(UUID.randomUUID());
//        properties.add(property);
//
//        model = QuantumComputationModel.GATE_BASED;
//
//        backend1 = new Backend();
//        backend1.setBackendProperties(properties);
//        backend1.setVendor("vendor1");
//        backend1.setName("backend1");
//        backend1.setTechnology("technology1");
//        backend1.setQuantumComputationModel(model);
//        backend1.setId(UUID.randomUUID());
//        backendList.add(backend1);
//
//        Backend backend2 = new Backend();
//        backend2.setBackendProperties(properties);
//        backend2.setVendor("vendor2");
//        backend2.setName("backend2");
//        backend2.setTechnology("technology2");
//        backend2.setQuantumComputationModel(model);
//        backend2.setId(UUID.randomUUID());
//        backendList.add(backend2);
//
//        backendDto1 = ModelMapperUtils.convert(backend1, BackendDto.class);
//    }
//
//    @Test
//    public void equality() throws JsonProcessingException {
//        assertEquals(backend1, backend1);
//        assertEquals(backend1, ModelMapperUtils.convert(mapper.readValue(
//                mapper.writeValueAsString(backendDto1), BackendDto.class), Backend.class));
//    }
//
//    @Test
//    public void getBackends() throws Exception {
//        Page<Backend> pageBackend = new PageImpl<>(backendList);
//        Page<BackendDto> pageBackendDto = ModelMapperUtils.convertPage(pageBackend, BackendDto.class);
//
//        when(service.findAll(pageable)).thenReturn(pageBackend);
//        when(paginationAssembler.toModel(ArgumentMatchers.any())).thenReturn(HateoasUtils.generatePagedModel(pageBackendDto));
//        doNothing().when(backendAssembler).addLinks(ArgumentMatchers.<Collection<EntityModel<BackendDto>>>any());
//
//        MvcResult res = mockMvc
//                .perform(get("/" + Constants.BACKENDS + "/").queryParam(Constants.PAGE, Integer.toString(page))
//                        .queryParam(Constants.SIZE, Integer.toString(size)).accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();
//
//        List<BackendDto> resultList = ObjectMapperUtils.mapResponseToList(res.getResponse().getContentAsString(),
//                "backendDtoes", BackendDto.class);
//        assertEquals(2, resultList.size());
//        //sort arrays to compare without checking the order
//        List<BackendDto> compareList = pageBackendDto.getContent();
//        compareList = sortDto(compareList);
//        resultList = sortDto(sortDto(resultList));
//        assertEquals(compareList, resultList);
//    }
//
//    @Test
//    public void getBackend_returnNotFound() throws Exception {
//        when(service.findById(any(UUID.class))).thenThrow(new NoSuchElementException());
//
//        mockMvc.perform(get("/" + Constants.BACKENDS + "/" + UUID.randomUUID()).accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    public void getBackend() throws Exception {
//        when(service.findById(any(UUID.class))).thenReturn(backend1);
//        doNothing().when(backendAssembler).addLinks(ArgumentMatchers.<EntityModel<BackendDto>>any());
//
//        MvcResult result = mockMvc
//                .perform(get("/" + Constants.BACKENDS + "/" + backend1.getId()).accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk()).andReturn();
//
//        EntityModel<BackendDto> response = new ObjectMapper().readValue(result.getResponse().getContentAsString(),
//                new TypeReference<EntityModel<BackendDto>>() {
//                });
//        Assertions.assertEquals(response.getContent().getId(), backendDto1.getId());
//    }
//
//    @Test
//    public void createBackend_returnBackRequest() throws Exception {
//        BackendDto backendDto = new BackendDto();
//        backendDto.setId(UUID.randomUUID());
//        mockMvc.perform(post("/" + Constants.BACKENDS + "/").content(mapper.writeValueAsString(backendDto))
//                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void createBackend() throws Exception {
//        when(service.saveOrUpdate(backend1)).thenReturn(backend1);
//        doNothing().when(backendAssembler).addLinks(ArgumentMatchers.<EntityModel<BackendDto>>any());
//
//        MvcResult result = mockMvc
//                .perform(post("/" + Constants.BACKENDS + "/").content(mapper.writeValueAsString(backendDto1))
//                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isCreated()).andReturn();
//
//        EntityModel<BackendDto> response = mapper.readValue(result.getResponse().getContentAsString(),
//                new TypeReference<EntityModel<BackendDto>>() {
//                });
//        Assertions.assertEquals(response.getContent(), this.backendDto1);
//    }
//
//    @Test
//    public void updateBackend_returnBadRequest() throws Exception {
//        BackendDto backendDto = new BackendDto();
//        backendDto.setId(UUID.randomUUID());
//        mockMvc.perform(put("/" + Constants.BACKENDS + "/{id}", backendDto.getId()).content(mapper.writeValueAsString(backendDto))
//                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void updateBackend() throws Exception {
//        when(service.saveOrUpdate(backend1)).thenReturn(backend1);
//        doNothing().when(backendAssembler).addLinks(ArgumentMatchers.<EntityModel<BackendDto>>any());
//
//        MvcResult result = mockMvc.perform(put("/" + Constants.BACKENDS + "/{id}", backend1.getId())
//                .content(mapper.writeValueAsString(backendDto1))
//                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isCreated()).andReturn();
//
//        EntityModel<BackendDto> response = mapper.readValue(result.getResponse().getContentAsString(),
//                new TypeReference<EntityModel<BackendDto>>() {
//                });
//        Assertions.assertEquals(response.getContent(), this.backendDto1);
//    }
//
//    @Test
//    public void deleteBackend_returnNotFound() throws Exception {
//        doThrow(new NoSuchElementException()).when(service).delete(any(UUID.class));
//        mockMvc.perform(delete("/" + Constants.BACKENDS + "/{id}", UUID.randomUUID()))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    public void deleteBackend() throws Exception {
//        mockMvc.perform(delete("/" + Constants.BACKENDS + "/{id}", backend1.getId()))
//                .andExpect(status().isOk()).andReturn();
//    }
//
//    @Test
//    public void inheritance() throws JsonProcessingException {
//        final Qpu qpu = new Qpu();
//        backend1.setBackendProperties(properties);
//        backend1.setVendor("vendor1");
//        backend1.setName("backend1");
//        backend1.setTechnology("technology1");
//        backend1.setQuantumComputationModel(model);
//        backend1.setId(UUID.randomUUID());
//
//        assertEquals(qpu, qpu);
//        assertEquals(qpu, ModelMapperUtils.convert(mapper.readValue(
//                mapper.writeValueAsString(qpu), BackendDto.class), Qpu.class));
//
//        final Simulator sim = new Simulator();
//        sim.setBackendProperties(properties);
//        sim.setVendor("vendor1");
//        sim.setName("backend1");
//        sim.setTechnology("technology1");
//        sim.setQuantumComputationModel(model);
//        sim.setId(UUID.randomUUID());
//        sim.setLocalExecution(false);
//
//        assertEquals(sim, sim);
//        assertEquals(sim, ModelMapperUtils.convert(mapper.readValue(
//                mapper.writeValueAsString(sim), BackendDto.class), Simulator.class));
//
//    }
//
//    private List<BackendDto> sortDto(List<BackendDto> backendDtos) {
//        // in case the list is not modifiable
//        List<BackendDto> toSort = new ArrayList<>(backendDtos);
//        toSort.sort(Comparator.comparing(BackendDto::getId));
//        return toSort;
//    }
//}
