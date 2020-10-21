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

import org.planqk.atlas.web.Constants;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RootController.class)
@AutoConfigureMockMvc
public class RootControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetHateoasLinks() throws Exception {
        MvcResult result = mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andReturn();

        var responseObject = new JSONObject(result.getResponse().getContentAsString());

        var linkArray = responseObject.getJSONObject("_links");
        assertEquals(7, linkArray.length());
        assertTrue(linkArray.has("self"));
        assertTrue(linkArray.has(Constants.ALGORITHMS));
        assertTrue(linkArray.has(Constants.PUBLICATIONS));
        assertFalse(linkArray.has("randomLink"));
    }
}
