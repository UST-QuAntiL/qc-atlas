package org.planqk.atlas.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.planqk.atlas.core.services.CloudServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {CloudServiceController.class})
@ExtendWith({MockitoExtension.class})
@AutoConfigureMockMvc
public class CloudServiceControllerTest {
    @MockBean
    private CloudServiceService cloudServiceService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void addCloudService_returnBadRequest() {

    }

    @Test
    public void addCloudService_returnCreate() {

    }

    @Test
    public void getCloudServices_withEmptySet() {

    }

    @Test
    public void getCloudServices_withOneElement() {

    }

    @Test
    public void getCloudServices_withMultipleElements() {

    }

    @Test
    public void getCloudService_returnNotFound() {

    }

    @Test
    public void getCloudService_returnElement() {

    }

    @Test
    public void deleteCloudService_returnNotFound() {

    }

    @Test
    public void deleteCloudService_returnOk() {

    }
}
