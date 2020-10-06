package org.planqk.atlas.core.services;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.Image;
import org.planqk.atlas.core.model.Sketch;
import org.planqk.atlas.core.repository.AlgorithmRepository;
import org.planqk.atlas.core.repository.ImageRepository;
import org.planqk.atlas.core.repository.SketchRepository;
import org.planqk.atlas.core.util.ServiceUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeType;
import org.springframework.web.multipart.MultipartFile;

import jdk.jfr.ContentType;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@Service
@AllArgsConstructor
public class SketchServiceImpl implements SketchService {

    private final SketchRepository sketchRepository;

    private final AlgorithmService algorithmService;

    private final ImageRepository imageRepository;

    private final AlgorithmRepository algorithmRepository;

    @Override
    @Transactional
    public Sketch update(@NonNull Sketch sketch) {
        final Sketch persistedSketch = ServiceUtils.findById(sketch.getId(), Sketch.class, sketchRepository);
        persistedSketch.setDescription(sketch.getDescription());
        return this.sketchRepository.save(persistedSketch);
    }

    @Override
    public List<Sketch> findByAlgorithm(UUID algorithmId) {
        return this.sketchRepository.findSketchesByAlgorithmId(algorithmId);
    }

    @Override
    @Transactional
    public Sketch addSketchToAlgorithm(UUID algorithmId, MultipartFile file, String description, String baseURL) {
        try {
            // Sketch
            Sketch sketch = new Sketch();
            sketch.setDescription(description);
            final Algorithm algorithm = algorithmService.findById(algorithmId);
            sketch.setAlgorithm(algorithm);
            final Sketch persistedSketch = sketchRepository.save(sketch);
            URL imageUrl = new URL(baseURL + "/algorithms/" + algorithmId + "/sketches/" + persistedSketch.getId());
            persistedSketch.setImageURL(imageUrl);
            Sketch persistedSketch2 = sketchRepository.save(persistedSketch);
            // image
            final Image image = new Image();
            image.setId(sketch.getId());
            image.setImage(file.getBytes());
            image.setMimeType(file.getContentType());
            image.setSketch(persistedSketch2);
            this.imageRepository.save(image);
            return sketch;
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot read contents of multipart file");
        }
    }

    @Override
    @Transactional
    public void delete(UUID sketchId) {
        sketchRepository.deleteById(sketchId);
    }

    @Override
    public Sketch findById(UUID sketchId) {
        return ServiceUtils.findById(sketchId, Sketch.class, sketchRepository);
    }

    @Override
    public Image getImageBySketch(UUID sketchId) {
        return this.imageRepository.findImageBySketchId(sketchId);
    }

}
