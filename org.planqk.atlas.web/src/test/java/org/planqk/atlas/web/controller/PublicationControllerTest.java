package org.planqk.atlas.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.services.PublicationService;
import org.planqk.atlas.web.Constants;
import org.planqk.atlas.web.dtos.*;
import org.planqk.atlas.web.linkassembler.PublicationAssembler;
import org.planqk.atlas.web.utils.HateoasUtils;
import org.planqk.atlas.web.utils.ModelMapperUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.net.URL;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers={PublicationController.class})
@ExtendWith({MockitoExtension.class})
@AutoConfigureMockMvc
public class PublicationControllerTest {

    @Mock
    private PublicationService publicationService;

    @Mock
    private PublicationAssembler publicationAssembler;

    @Mock
    private PagedResourcesAssembler<PublicationDto> paginationAssembler;

    @InjectMocks
    private PublicationController publicationController;


    private MockMvc mockMvc;
    private ObjectMapper mapper;

    private int page=0;
    private int size=1;
    private Pageable pageable=PageRequest.of(page, size);
    private PublicationDto publicationDto;
    private Publication publication;

    @Before
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
        mapper=new ObjectMapper();
        mockMvc=MockMvcBuilders.standaloneSetup(publicationController).setControllerAdvice(new RestErrorHandler()).build();
        mapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
        publication=new Publication();
        publication.setId(UUID.randomUUID());
        publication.setAuthors(new ArrayList<>(Arrays.asList("author1", "author2")));
        publication.setUrl(new URL("http://www.atlas-is-cool.org"));
        publication.setTitle("TestPublication");
        publication.setDoi("");

        publicationDto=ModelMapperUtils.convert(publication, PublicationDto.class);
        when(publicationService.findById(publication.getId())).thenReturn(publication);
    }

    @Test
    public void contextLoaded() throws Exception {
        assertNotNull(publicationController);
        assertNotNull(mockMvc);
    }

    @Test
    public void getPublications_PublicationList() throws Exception {
        List<Publication> publications=new ArrayList<>();
        publications.add(publication);
        Page<Publication> pagePublication=new PageImpl<>(publications);
        Page<PublicationDto> pagePublicationDto=ModelMapperUtils.convertPage(pagePublication, PublicationDto.class);

        when(publicationService.findAll(pageable)).thenReturn(pagePublication);
        when(paginationAssembler.toModel(ArgumentMatchers.<Page<PublicationDto>>any()))
                .thenReturn(HateoasUtils.generatePagedModel(pagePublicationDto));
        doNothing().when(publicationAssembler).addLinks(ArgumentMatchers.<Collection<EntityModel<PublicationDto>>>any());

        MvcResult result=mockMvc
                .perform(get("/" + Constants.PUBLICATIONS + "/").queryParam(Constants.PAGE, Integer.toString(page))
                        .queryParam(Constants.SIZE, Integer.toString(size)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();

        PagedModel<EntityModel<PublicationDto>> publicationListDto=mapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<PagedModel<EntityModel<PublicationDto>>>() {
                });
        assertEquals(publicationListDto.getContent().size(), 1);
    }

    @Test
    public void getPublications_emptyPublicationList() throws Exception {
        when(publicationService.findAll(pageable)).thenReturn(Page.empty());
        when(paginationAssembler.toModel(ArgumentMatchers.<Page<PublicationDto>>any()))
                .thenReturn(HateoasUtils.generatePagedModel(Page.empty()));
        doNothing().when(publicationAssembler).addLinks(ArgumentMatchers.<Collection<EntityModel<PublicationDto>>>any());
        MvcResult mvcResult=mockMvc.perform(get("/" + Constants.PUBLICATIONS + "/").queryParam(Constants.PAGE, Integer.toString(page))
                .queryParam(Constants.SIZE, Integer.toString(size)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        PagedModel<EntityModel<PublicationDto>> pagedModel=mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<PagedModel<EntityModel<PublicationDto>>>() {
        });
        assertEquals(pagedModel.getContent().size(), 0);
    }

    @Test
    public void createPublication_returnPublication() throws Exception {
        when(publicationService.save(publication)).thenReturn(publication);
        doNothing().when(publicationAssembler).addLinks(ArgumentMatchers.<EntityModel<PublicationDto>>any());
        MvcResult result=mockMvc
                .perform(post("/" + Constants.PUBLICATIONS + "/").content(mapper.writeValueAsString(publicationDto))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        EntityModel<PublicationDto> response=mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<EntityModel<PublicationDto>>() {
                });
        assertEquals(response.getContent().getTitle(), publicationDto.getTitle());
        assertEquals(response.getContent().getDoi(), publicationDto.getDoi());
        assertEquals(response.getContent().getUrl(), publicationDto.getUrl());
    }
}
