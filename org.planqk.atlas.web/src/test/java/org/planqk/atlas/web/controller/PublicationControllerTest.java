package org.planqk.atlas.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.services.PublicationService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.net.URL;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class PublicationControllerTest {

    @Mock
    private PublicationService publicationService;

    @InjectMocks
    private PublicationController publicationController;

    private MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    private int page = 0;
    private int size = 1;
    private Pageable pageable = PageRequest.of(page,size);

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(publicationController).build();
    }

    /**
     * Create publication for test
     * @return
     * @throws Exception
     */
    private static Publication getPublication() throws Exception{

        Publication publication = new Publication();
        publication.setId(UUID.randomUUID());
        publication.setAuthors(new ArrayList<>(Arrays.asList("author1","author2")));
        publication.setUrl(new URL("http://www.atlas-is-cool.org"));
        publication.setTitle("TestPublication");

        return publication;
    }

    @Test
    public void contextLoaded() throws Exception {
        assertThat(publicationController).isNotNull();
    }

    @Test
    public void getPublications() throws Exception {

        List<Publication> publicationList = new ArrayList<>(Arrays.asList(getPublication()));
        Page<Publication> publicationPage = new PageImpl<Publication>(publicationList, pageable, publicationList.size());

        when(publicationService.findAll(any(Pageable.class))).thenReturn(publicationPage);
        MvcResult mvcResult = mockMvc.perform(get("/" + Constants.PUBLICATIONS + "/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        PublicationListDto publicationListDto = mapper.readValue(mvcResult.getResponse().getContentAsString(), PublicationListDto.class);
        assertEquals(publicationListDto.getPublicationDtos().size(), 1);
        assertEquals(publicationListDto.getPublicationDtos().get(0).getTitle(),publicationList.get(0).getTitle());
        assertEquals(publicationListDto.getPublicationDtos().get(0).getUrl(),publicationList.get(0).getUrl());
        assertEquals(publicationListDto.getPublicationDtos().get(0).getDoi(),publicationList.get(0).getDoi());
    }

    @Test
    public void getPublication_returnNotFound() throws Exception {

        this.mockMvc.perform(get("/"+ Constants.PUBLICATIONS+"/"+UUID.randomUUID()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());

    }

    @Test
    public void createPublication_returnBadRequest() throws Exception {

        PublicationDto emptyPublicationDto = new PublicationDto();
        mockMvc.perform(post("/" + Constants.PUBLICATIONS + "/")
                .content(mapper.writeValueAsString(emptyPublicationDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void getPublication_returnPublication() throws Exception {

        when(publicationService.findById(any(UUID.class))).thenReturn(java.util.Optional.of(getPublication()));

        MvcResult mvcResult = mockMvc.perform(get("/" + Constants.PUBLICATIONS + "/" + getPublication().getId() + "/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        PublicationDto createPublicationDto = mapper.readValue(mvcResult.getResponse().getContentAsString(), PublicationDto.class);
        assertEquals(createPublicationDto.getTitle(), getPublication().getTitle());
    }

    @Test
    public void getAlgorithmsOfPublication() throws  Exception {

        //TODO
        fail();
    }
}
