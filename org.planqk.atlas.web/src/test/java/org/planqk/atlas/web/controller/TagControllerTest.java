/*******************************************************************************
 * Copyright (c) 2020 University of Stuttgart
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
// Tags will be used/tested and included in the future

//@WebMvcTest(TagController.class)
//@ExtendWith(MockitoExtension.class)
//@AutoConfigureMockMvc
//@EnableLinkAssemblers
//public class TagControllerTest {
//    @MockBean
//    private TagService tagService;
//    @MockBean
//    private TagAssembler tagAssembler;
//    @MockBean
//    private PagedResourcesAssembler<TagDto> paginationAssembler;
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    private ObjectMapper mapper;
//
//    @BeforeEach
//    public void before() {
//        this.mapper = ObjectMapperUtils.newTestMapper();
//    }
//
//    private Tag getTestTag() {
//        UUID tagId = UUID.randomUUID();
//        Tag tag1 = new Tag();
//        tag1.setId(tagId);
//        tag1.setKey("testkey");
//        tag1.setValue("testvalue");
//        return tag1;
//    }
//
//    @Test
//    public void testGetAllTags() throws Exception {
//        List<Tag> tags = new ArrayList<>();
//        Tag tag1 = getTestTag();
//        tags.add(tag1);
//        tags.add(new Tag());
//        Pageable pageable = PageRequest.of(0, 2);
//
//        Page<Tag> page = new PageImpl<Tag>(tags, pageable, tags.size());
//        Page<TagDto> pageDto = ModelMapperUtils.convertPage(page, TagDto.class);
//
//        when(tagService.findAll(any(Pageable.class))).thenReturn(page);
//        when(paginationAssembler.toModel(ArgumentMatchers.any())).thenReturn(HateoasUtils.generatePagedModel(pageDto));
//        doNothing().when(tagAssembler).addLinks(ArgumentMatchers.<Collection<EntityModel<TagDto>>>any());
//
//        MvcResult result = mockMvc.perform(get("/" + Constants.TAGS + "/").accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk()).andReturn();
//        var resultList = ObjectMapperUtils.mapResponseToList(result.getResponse().getContentAsString(), "tagDtoes",
//                TagDto.class);
//        assertEquals(2, resultList.size());
//    }
//
//    @Test
//    public void getTags_withEmptyTagList() throws Exception {
//        when(tagService.findAll(any(Pageable.class))).thenReturn(Page.empty());
//        when(paginationAssembler.toModel(ArgumentMatchers.any()))
//                .thenReturn(HateoasUtils.generatePagedModel(Page.empty()));
//        doNothing().when(tagAssembler).addLinks(ArgumentMatchers.<Collection<EntityModel<TagDto>>>any());
//
//        MvcResult result = mockMvc
//                .perform(get("/" + Constants.TAGS + "/").queryParam(Constants.PAGE, Integer.toString(0))
//                        .queryParam(Constants.SIZE, Integer.toString(4)).accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk()).andReturn();
//
//        var resultList = ObjectMapperUtils.mapResponseToList(result.getResponse().getContentAsString(), "tagDtoes",
//                TagDto.class);
//        assertEquals(0, resultList.size());
//    }
//
//    @Test
//    public void testGetId() throws Exception {
//        Tag tag1 = getTestTag();
//        when(tagService.getTagById(any(UUID.class))).thenReturn(tag1);
//        doNothing().when(tagAssembler).addLinks(ArgumentMatchers.<EntityModel<TagDto>>any());
//
//        MvcResult mvcResult = mockMvc
//                .perform(get("/" + Constants.TAGS + "/" + tag1.getId() + "/").accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk()).andReturn();
//
//        EntityModel<TagDto> createdTag = mapper.readValue(mvcResult.getResponse().getContentAsString(),
//                new TypeReference<EntityModel<TagDto>>() {
//                });
//        assertEquals(createdTag.getContent().getKey(), tag1.getKey());
//        assertEquals(createdTag.getContent().getValue(), tag1.getValue());
//    }
//
//    @Test
//    public void testPostTag() throws Exception {
//        Tag tag1 = getTestTag();
//
//        when(tagService.save(tag1)).thenReturn(tag1);
//        doNothing().when(tagAssembler).addLinks(ArgumentMatchers.<EntityModel<TagDto>>any());
//
//        MvcResult result = mockMvc.perform(
//                MockMvcRequestBuilders.post("/" + Constants.TAGS + "/").content(TestControllerUtils.asJsonString(tag1))
//                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isCreated()).andReturn();
//
//        EntityModel<TagDto> createdTag = mapper.readValue(result.getResponse().getContentAsString(),
//                new TypeReference<EntityModel<TagDto>>() {
//                });
//        assertEquals(createdTag.getContent().getKey(), tag1.getKey());
//        assertEquals(createdTag.getContent().getValue(), tag1.getValue());
//    }
}
