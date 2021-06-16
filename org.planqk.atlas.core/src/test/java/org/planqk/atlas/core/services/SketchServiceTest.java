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

package org.planqk.atlas.core.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ApplicationArea;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.Image;
import org.planqk.atlas.core.model.Sketch;
import org.planqk.atlas.core.repository.ImageRepository;
import org.planqk.atlas.core.repository.SketchRepository;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

public class SketchServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private SketchService sketchService;

    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private SketchRepository sketchRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Test
    void updateSketch() {
        final Algorithm algorithm = this.algorithmService.create(this.getAlgorithm("algo"));

        byte[] testFile = hexStringToByteArray("e04fd020ea3a6910a2d808002b30309d");
        final MockMultipartFile file = new MockMultipartFile("image", testFile);

        final String description = "description";
        final String baseURL = "http://localhost:6626/atlas";
        // call
        final Sketch persistedSketch = sketchService.addSketchToAlgorithm(algorithm.getId(), file, description, baseURL);

        final String updateDescription = "updatedDescription";
        Sketch updateSketch = new Sketch();
        updateSketch.setId(persistedSketch.getId());
        updateSketch.setDescription(updateDescription);

        sketchService.update(updateSketch);

        Sketch persistedUpdatedSketch = sketchService.findById(persistedSketch.getId());

        assertThat(persistedUpdatedSketch.getId()).isEqualTo(persistedSketch.getId());
        assertThat(persistedUpdatedSketch.getImageURL()).isEqualTo(persistedSketch.getImageURL());
        assertThat(persistedUpdatedSketch.getImage()).isNotNull();
        assertThat(persistedUpdatedSketch.getAlgorithm().getId()).isEqualTo(persistedSketch.getAlgorithm().getId());
        assertThat(persistedUpdatedSketch.getDescription()).isEqualTo(updateDescription);
    }

    @Test
    void findSketchByAlgorithm() {

        // mock
        final Algorithm algorithm = this.algorithmService.create(this.getAlgorithm("algo"));

        final Sketch sketch = this.getSketch(null, "http://image/url", "description");
        sketch.setAlgorithm(algorithm);
        final Sketch persistedSketch = this.sketchRepository.save(sketch);

        // call
        final List<Sketch> persistedSketches = sketchService.findByAlgorithm(algorithm.getId());

        // test
        persistedSketches.forEach(s -> this.assertSketchEquality(s, persistedSketch));
    }

    @Test
    void addSketchToAlgorithm() {
        // mock
        final Algorithm algorithm = this.algorithmService.create(this.getAlgorithm("algo"));

        byte[] testFile = hexStringToByteArray("e04fd020ea3a6910a2d808002b30309d");
        final MockMultipartFile file = new MockMultipartFile("image", testFile);

        final String description = "description";
        final String baseURL = "http://localhost:6626/atlas";
        // call
        final Sketch persistedSketch = sketchService.addSketchToAlgorithm(algorithm.getId(), file, description, baseURL);

        // test
        assertThat(persistedSketch.getId()).isNotNull();
        assertThat(persistedSketch.getDescription()).isEqualTo(description);
        assertThat(persistedSketch.getImageURL())
                .startsWith(baseURL);
        assertEquals(persistedSketch.getImageURL(),
                baseURL + "/algorithms/" + algorithm.getId() + "/sketches/" + persistedSketch.getId());

        List<Image> images = this.imageRepository.findAll();
        assertThat(images.size()).isEqualTo(1);
    }

    @Test
    void deleteSketch() {
        // mock
        final Algorithm algorithm = this.algorithmService.create(this.getAlgorithm("algo"));

        final Sketch sketch = this.getSketch(null, "http://image/url", "description");
        sketch.setAlgorithm(algorithm);
        Sketch persistedSketch = this.sketchRepository.save(sketch);

        assertDoesNotThrow(() -> this.sketchService.findById(persistedSketch.getId()));

        // call
        this.sketchService.delete(persistedSketch.getId());

        // test
        assertThrows(NoSuchElementException.class, () -> sketchService.findById(persistedSketch.getId()));
    }

    @Test
    void findSketchById_ElementFound() {

        // mock
        final Algorithm algorithm = this.algorithmService.create(this.getAlgorithm("algo"));

        final Sketch sketch = this.getSketch(null, "http://image/url", "description");
        sketch.setAlgorithm(algorithm);
        final Sketch persistedSketch = this.sketchRepository.save(sketch);

        // call
        final Sketch response = this.sketchService.findById(persistedSketch.getId());

        // test
        assertThat(response).isNotNull();
    }

    @Test
    void getImageBySketch() {

        // mock
        final Algorithm algorithm = this.algorithmService.create(this.getAlgorithm("algo"));

        final Sketch sketch = this.getSketch(null, "http://image/url", "description");
        sketch.setAlgorithm(algorithm);
        final Sketch persistedSketch = this.sketchRepository.save(sketch);

        byte[] testFile = hexStringToByteArray("e04fd020ea3a6910a2d808002b30309d");
        final Image image = new Image();
        image.setImage(testFile);
        image.setSketch(persistedSketch);
        this.imageRepository.save(image);

        // call
        final byte[] response = this.sketchService.getImageBySketch(persistedSketch.getId()).getImage();

        // test
        assertTrue(Arrays.equals(response, testFile));
    }

    private Sketch getSketch(final Image image, final String imageURLAsString, final String description) {
        final Sketch sketch = new Sketch();
        sketch.setImage(image);
        sketch.setImageURL(imageURLAsString);
        sketch.setDescription(description);
        return sketch;
    }

    private void assertSketchEquality(final Sketch persistedSketch, final Sketch compareSketch) {
        assertThat(persistedSketch.getId()).isNotNull();
        assertThat(persistedSketch.getImage()).isEqualTo(compareSketch.getImage());
        assertThat(persistedSketch.getImageURL()).isEqualTo(compareSketch.getImageURL());
        assertThat(persistedSketch.getDescription()).isEqualTo(compareSketch.getDescription());
    }

    private Algorithm getAlgorithm(String name) {
        Algorithm algorithm = new ClassicAlgorithm();
        algorithm.setName(name);
        algorithm.setAcronym("testAcronym");
        algorithm.setIntent("testIntent");
        algorithm.setProblem("testProblem");
        algorithm.setInputFormat("testInputFormat");
        algorithm.setAlgoParameter("testAlgoParameter");
        algorithm.setOutputFormat("testOutputFormat");
        algorithm.setSketches(new ArrayList<>());
        algorithm.setSolution("testSolution");
        algorithm.setAssumptions("testAssumptions");
        algorithm.setComputationModel(ComputationModel.CLASSIC);
        Set<ApplicationArea> applicationAreas = new HashSet<>();
        ApplicationArea applicationArea = new ApplicationArea();
        applicationArea.setName("test");
        applicationAreas.add(applicationArea);
        algorithm.setApplicationAreas(applicationAreas);
        return algorithm;
    }

    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}




