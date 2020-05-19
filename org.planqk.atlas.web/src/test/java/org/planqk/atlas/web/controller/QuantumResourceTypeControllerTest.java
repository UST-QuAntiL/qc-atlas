package org.planqk.atlas.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import org.apache.tomcat.util.bcel.Const;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.planqk.atlas.core.model.QuantumResourceDataType;
import org.planqk.atlas.core.model.QuantumResourceType;
import org.planqk.atlas.core.services.QuantumResourceTypeService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.AlgorithmListDto;
import org.planqk.atlas.web.dtos.QuantumResourceTypeDto;
import org.planqk.atlas.web.dtos.QuantumResourceTypeListDto;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class QuantumResourceTypeControllerTest {

    @Mock
    private QuantumResourceTypeService typeService;

    @InjectMocks
    private QuantumResourceTypeController controller;

    private int page = 0;
    private int size = 2;
    private Pageable pageable = PageRequest.of(page, size);

    private MockMvc mockMvc;

    @Before
    public void initialize() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void test_listTypes_MissingPagination() throws Exception {
        when(typeService.findAll(Pageable.unpaged())).thenReturn(Page.empty());
        mockMvc.perform(get("/" + Constants.QUANTUM_RESOURCE_TYPES + "/")
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void test_listTypes_EmptyList() throws Exception {
        when(typeService.findAll(pageable)).thenReturn(Page.empty());
        mockMvc.perform(get("/" + Constants.QUANTUM_RESOURCE_TYPES + "/")
                .queryParam(Constants.PAGE, page + "")
                .queryParam(Constants.SIZE, size + "")
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void test_listTypes_List() throws Exception {
        var typeList = new ArrayList<QuantumResourceType>();
        var t1 = new QuantumResourceType();
        t1.setId(UUID.randomUUID());
        t1.setDataType(QuantumResourceDataType.FLOAT);
        t1.setName("test-float-type");
        t1.setDescription("Some text");
        typeList.add(t1);

        var t2 = new QuantumResourceType();
        t2.setId(UUID.randomUUID());
        t2.setDataType(QuantumResourceDataType.STRING);
        t2.setName("test-string-type");
        t2.setDescription("Some text");
        typeList.add(t2);

        when(typeService.findAll(pageable)).thenReturn(new PageImpl<>(typeList));
        var result = mockMvc.perform(get("/" + Constants.QUANTUM_RESOURCE_TYPES + "/")
                .queryParam(Constants.PAGE, page + "")
                .queryParam(Constants.SIZE, size + "")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        var response = new ObjectMapper().readValue(result.getResponse().getContentAsString(), QuantumResourceTypeListDto.class);
        assertEquals(2, response.getItems().size());
    }

    @Test
    public void test_getType_InvalidId() throws Exception {
        var id = UUID.randomUUID();
        when(typeService.findById(any())).thenReturn(Optional.empty());
        mockMvc.perform(get("/" + Constants.QUANTUM_RESOURCE_TYPES + "/" + id.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void test_getType_MalformedId() throws Exception {
        when(typeService.findById(any())).thenReturn(Optional.empty());
        mockMvc.perform(get("/" + Constants.QUANTUM_RESOURCE_TYPES + "/abc"))
                .andExpect(status().isBadRequest());
    }
}
