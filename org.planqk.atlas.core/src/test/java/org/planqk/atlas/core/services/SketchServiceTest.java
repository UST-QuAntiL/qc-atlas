package org.planqk.atlas.core.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
    void testFindByAlgorithm() {

        // mock
        final Algorithm algorithm = this.algorithmService.save(this.getAlgorithm("algo"));

        final Sketch sketch = this.getSketch(null, "image/url", "description");
        sketch.setAlgorithm(algorithm);
        final Sketch persistedSketch = this.sketchRepository.save(sketch);

        // call
        final List<Sketch> persistedSketches = sketchService.findByAlgorithm(algorithm.getId());

        // test
        persistedSketches.forEach(s -> this.assertSketchEquality(s, persistedSketch));
    }

    @Test
    void testAddSketchToAlgorithm() {

        // mock
        final Algorithm algorithm = this.algorithmService.save(this.getAlgorithm("algo"));

        byte[] testFile = hexStringToByteArray("e04fd020ea3a6910a2d808002b30309d");
        final MockMultipartFile file = new MockMultipartFile("file", testFile);

        final String description = "description";
        final String baseURL = "base/URL";
        // call
        final Sketch persistedSketch = sketchService.addSketchToAlgorithm(algorithm.getId(), file, description, baseURL);

        // test
        assertThat(persistedSketch.getId()).isNotNull();
        assertThat(persistedSketch.getDescription()).isEqualTo(description);
        assertThat(persistedSketch.getImageURL()).startsWith(baseURL);

        List<Image> images = this.imageRepository.findAll();
        assertThat(images.size()).isEqualTo(1);
    }

    @Test
    void testDelete() {

        // mock
        final Algorithm algorithm = this.algorithmService.save(this.getAlgorithm("algo"));

        final Sketch sketch = this.getSketch(null, "image/url", "description");
        sketch.setAlgorithm(algorithm);
        final Sketch response = this.sketchRepository.save(sketch);

        final Sketch persistedSketch = this.sketchService.findById(response.getId());
        assertThat(persistedSketch).isNotNull();
        // call
        this.sketchService.delete(response.getId());

        // test
        final Sketch deletedSketch = this.sketchService.findById(response.getId());
        assertThat(deletedSketch).isNull();
    }

    @Test
    void testFindById() {

        // mock
        final Algorithm algorithm = this.algorithmService.save(this.getAlgorithm("algo"));

        final Sketch sketch = this.getSketch(null, "image/url", "description");
        sketch.setAlgorithm(algorithm);
        final Sketch persistedSketch = this.sketchRepository.save(sketch);

        // call
        final Sketch response = this.sketchService.findById(persistedSketch.getId());

        // test
        assertThat(response).isNotNull();
    }

    @Test
    void testGetSketchByAlgorithmAndSketch() {

        // mock
        final Algorithm algorithm = this.algorithmService.save(this.getAlgorithm("algo"));

        final Sketch sketch = this.getSketch(null, "image/url", "description");
        sketch.setAlgorithm(algorithm);
        final Sketch persistedSketch = this.sketchRepository.save(sketch);

        // call
        final Sketch response = this.sketchService.findById(persistedSketch.getId());

        // test
        assertThat(response).isNotNull();
    }

    @Test
    void testGetImageByAlgorithmAndSketch() {

        // mock
        final Algorithm algorithm = this.algorithmService.save(this.getAlgorithm("algo"));

        final Sketch sketch = this.getSketch(null, "image/url", "description");
        sketch.setAlgorithm(algorithm);
        final Sketch persistedSketch = this.sketchRepository.save(sketch);

        byte[] testFile = hexStringToByteArray("e04fd020ea3a6910a2d808002b30309d");
        final Image image = new Image();
        image.setImage(testFile);
        image.setSketch(persistedSketch);
        this.imageRepository.save(image);

        // call
        final byte[] response = this.sketchService.getImageBySketch(persistedSketch.getId());

        // test
        assertTrue(Arrays.equals(response, testFile));
    }

    private Sketch getSketch(final Image image, final String imageURL, final String description) {
        final Sketch sketch = new Sketch();
        sketch.setImage(image);
        sketch.setImageURL(imageURL);
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




