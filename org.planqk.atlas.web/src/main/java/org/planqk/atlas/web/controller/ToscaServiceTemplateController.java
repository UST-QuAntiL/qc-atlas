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

package org.planqk.atlas.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.planqk.atlas.core.services.ToscaServiceTemplateService;
import org.planqk.atlas.web.Constants;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@io.swagger.v3.oas.annotations.tags.Tag(name = Constants.TAG_TOSCA)
@RestController
@CrossOrigin(allowedHeaders = "*", origins = "*")
@RequestMapping("/" + Constants.TOSCA_SERVICETEMPLATES)
@AllArgsConstructor
@Slf4j
public class ToscaServiceTemplateController {

    private final ToscaServiceTemplateService toscaServiceTemplateService;

    @Operation(responses = {
            @ApiResponse(responseCode = "200")
    }, description = "Retrieve all TOSCA servicetemplates.")
    @GetMapping()
    public String getServiceTemplates() {
        return this.toscaServiceTemplateService.getAll();
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200")
    }, description = "Retrieve a specific TOSCA servicetemplate.")
    @GetMapping("/{namespace}/{name}")
    public String getServiceTemplate(@PathVariable String namespace, @PathVariable String name) {
        return this.toscaServiceTemplateService.get(namespace, name);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200")
    }, description = "Retrieve a TOSCA servicetemplate.")
    @GetMapping("/{namespace}/{name}/selfserviceportal")
    public String getServiceTemplateSelfServicePortal(@PathVariable String namespace, @PathVariable String name) {
        return this.toscaServiceTemplateService.getSelfServicePortal(namespace, name);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200")
    }, description = "Retrieve the icon of a TOSCA servicetemplate.")
    @GetMapping(value = "/{namespace}/{name}/selfserviceportal/icon.jpg", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getServiceTemplateSelfServicePortalIcon(@PathVariable String namespace, @PathVariable String name) {
        return this.toscaServiceTemplateService.getSelfServicePortalIcon(namespace, name);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200")
    }, description = "Retrieve the image of a TOSCA servicetemplate.")
    @GetMapping(value = "/{namespace}/{name}/selfserviceportal/image.jpg", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getServiceTemplateSelfServicePortalImage(@PathVariable String namespace, @PathVariable String name) {
        return this.toscaServiceTemplateService.getSelfServicePortalImage(namespace, name);
    }
}
