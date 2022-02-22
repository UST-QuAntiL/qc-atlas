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

import java.io.ByteArrayInputStream;
import java.net.URI;

import lombok.SneakyThrows;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.planqk.atlas.core.model.ToscaApplication;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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

    private URIBuilder wineryEndPoint;

    @BeforeEach
    void init(){
        wineryService = new WineryService("http", "localhost", 8091, restTemplate);
        wineryEndPoint = new URIBuilder();
        wineryEndPoint.setScheme("http").setHost("localhost").setPort(8091);
    }

    @SneakyThrows
    @Test
    public void get_returnOK() {
        var testRoute = "this/is/the/test/route";
        var expectedResult = "Test-Body";
        Mockito.when(restTemplate.getForEntity(eq(wineryEndPoint.setPath(testRoute).build()), eq(String.class))).thenReturn(new ResponseEntity<>(expectedResult, HttpStatus.OK));
        String result = wineryService.get(testRoute);
        assertEquals(expectedResult, result);
    }

    @SneakyThrows
    @Test
    public void get_notFound() {
        var testRoute = "this/is/the/test/route";
        Mockito.when(restTemplate.getForEntity(eq(wineryEndPoint.setPath(testRoute).build()), eq(String.class))).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class, () -> wineryService.get(testRoute));
        assertEquals(HttpStatus.NOT_FOUND, responseStatusException.getStatus());
    }

    @Test
    @SneakyThrows
    public void uploadCsar_notFound(){
        var uploadRoute = "/winery/";
        Resource file = new InputStreamResource(new ByteArrayInputStream("test input stream".getBytes()));
        String name = "test-name";

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        final MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file);
        body.add("name", name);
        final HttpEntity<MultiValueMap<String, Object>> postRequestEntity = new HttpEntity<>(body, headers);

        Mockito.when(restTemplate.postForEntity(eq(wineryEndPoint.setPath(uploadRoute).build()), eq(postRequestEntity), eq(String.class))).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class, () -> wineryService.uploadCsar(file, name));
        assertEquals(HttpStatus.NOT_FOUND, responseStatusException.getStatus());
    }

    @Test
    @SneakyThrows
    public void uploadCsar_created(){
        var uploadRoute = "/winery/";
        var locationRoute = "/this/is/the/serviceTemplate/location";
        Resource file = new InputStreamResource(new ByteArrayInputStream("test input stream".getBytes()));
        String name = "test-name";

        String abbreviatedWineryJsonResponse = "{\n" +
                "\t\"documentation\": [\n" +
                "\t],\n" +
                "\t\"any\": [\n" +
                "\t],\n" +
                "\t\"otherAttributes\": {\n" +
                "\t},\n" +
                "\t\"id\": \"otsteIgeneral-Java_Web_Application__MySQL\",\n" +
                "\t\"serviceTemplateOrNodeTypeOrNodeTypeImplementation\": [\n" +
                "\t\t{\n" +
                "\t\t\t\"documentation\": [\n" +
                "\t\t\t],\n" +
                "\t\t\t\"any\": [\n" +
                "\t\t\t],\n" +
                "\t\t\t\"otherAttributes\": {\n" +
                "\t\t\t},\n" +
                "\t\t\t\"id\": \"Java_Web_Application__MySQL\",\n" +
                "\t\t\t\"boundaryDefinitions\": {\n" +
                "\t\t\t},\n" +
                "\t\t\t\"topologyTemplate\": {\n" +
                "\t\t\t},\n" +
                "\t\t\t\"name\": \"Java_Web_Application__MySQL\",\n" +
                "\t\t\t\"targetNamespace\": \"http://opentosca.org/servicetemplates\"\n" +
                "\t\t}\n" +
                "\t],\n" +
                "\t\"targetNamespace\": \"http://opentosca.org/servicetemplates\",\n" +
                "\t\"import\": [\t]\n" +
                "}";

        ToscaApplication expectedToscaApplication = new ToscaApplication();
        expectedToscaApplication.setToscaID("Java_Web_Application__MySQL");
        expectedToscaApplication.setToscaName("Java_Web_Application__MySQL");
        expectedToscaApplication.setToscaNamespace("http://opentosca.org/servicetemplates");
        expectedToscaApplication.setName(name);
        expectedToscaApplication.setWineryLocation(locationRoute);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        final MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file);
        body.add("name", name);
        final HttpEntity<MultiValueMap<String, Object>> postRequestEntity = new HttpEntity<>(body, headers);


        ResponseEntity<String> responseEntity = ResponseEntity.created(new URI(locationRoute)).build();

        Mockito.when(restTemplate.postForEntity(eq(wineryEndPoint.setPath(uploadRoute).build()), eq(postRequestEntity), eq(String.class))).thenReturn(responseEntity);
        Mockito.when(restTemplate.getForObject(eq(wineryEndPoint.setPath(locationRoute).build()), eq(String.class))).thenReturn(abbreviatedWineryJsonResponse);

        ToscaApplication toscaApplication = wineryService.uploadCsar(file, name);
        assertEquals(expectedToscaApplication, toscaApplication);
    }

    @SneakyThrows
    @Test
    public void delete() {
        var wineryLocation = "this/is/the/winery/location";
        ToscaApplication toscaApplication = new ToscaApplication();
        toscaApplication.setWineryLocation(wineryLocation);
        wineryService.delete(toscaApplication);
        Mockito.verify(restTemplate).delete(wineryEndPoint.setPath(wineryLocation).build());
    }


}
