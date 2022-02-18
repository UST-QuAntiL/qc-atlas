/*******************************************************************************
 * Copyright (c) 2020-2022 the qc-atlas contributors.
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

package org.planqk.atlas.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.planqk.atlas.core.model.ToscaApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
class WineryServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private WineryService wineryService;

    private final String wineryEndPoint = "http://localhost:8091/";

    @BeforeEach
    void init(){
        wineryService = new WineryService("http", "localhost", "8091", restTemplate);
    }

    @Test
    public void get_returnOK() {
        var testRoute = "this/is/the/test/route";
        var expectedResult = "Test-Body";
        Mockito.when(restTemplate.getForEntity(eq(wineryEndPoint + testRoute), eq(String.class))).thenReturn(new ResponseEntity<>(expectedResult, HttpStatus.OK));
        String result = wineryService.get(testRoute);
        assertEquals(expectedResult, result);
    }

    @Test
    public void get_notFound() {
        var testRoute = "this/is/the/test/route";
        Mockito.when(restTemplate.getForEntity(eq(wineryEndPoint + testRoute), eq(String.class))).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class, () -> wineryService.get(testRoute));
        assertEquals(HttpStatus.NOT_FOUND, responseStatusException.getStatus());
    }

    @Test
    public void delete() {
        var wineryLocation = "this/is/the/winery/location";
        ToscaApplication toscaApplication = new ToscaApplication();
        toscaApplication.setWineryLocation(wineryLocation);
        wineryService.delete(toscaApplication);
        Mockito.verify(restTemplate).delete(wineryEndPoint + wineryLocation);
    }


}
