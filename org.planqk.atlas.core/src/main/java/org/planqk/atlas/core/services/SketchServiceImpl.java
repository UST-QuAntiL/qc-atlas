package org.planqk.atlas.core.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.Image;
import org.planqk.atlas.core.model.Sketch;
import org.planqk.atlas.core.repository.AlgorithmRepository;
import org.planqk.atlas.core.repository.ImageRepository;
import org.planqk.atlas.core.repository.SketchRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SketchServiceImpl implements SketchService {

    private final SketchRepository sketchRepository;

    private final AlgorithmService algorithmService;

    private ImageRepository imageRepository;

    private AlgorithmRepository algorithmRepository;

    @Override
    public List<Sketch> findByAlgorithm(UUID algorithmId) {
        return this.sketchRepository.findSketchesByAlgorithm_Id(algorithmId);
    }

    @Override
    public Sketch addSketchToAlgorithm(UUID algorithmId, MultipartFile file, String description, String baseURL) {
        try {
            byte[] fileContent = file.getBytes();
            // Sketch
            Sketch sketch = new Sketch();
            sketch.setDescription(description);
            final Algorithm algorithm = algorithmService.findById(algorithmId);
            sketch.setAlgorithm(algorithm);
            final Sketch persistedSketch = sketchRepository.save(sketch);
            persistedSketch.setImageURL(baseURL + "/algorithms/" + algorithmId + "/sketches/" + persistedSketch.getId());
            Sketch persistedSketch2 = sketchRepository.save(persistedSketch);
            // image
            final Image image = new Image();
            image.setId(sketch.getId());
            image.setImage(fileContent);
            image.setSketch(persistedSketch2);
            this.imageRepository.save(image);


            return sketch;
        } catch (Exception e) {
            throw new RuntimeException("Could not store the Sketch. Error: " + e.getMessage());
        }
    }

    @Override
    public void delete(UUID sketchId) {
        sketchRepository.deleteById(sketchId);
    }

    @Override
    public Sketch findById(UUID id) {
        final Optional<Sketch> sketchOptional = this.sketchRepository.findById(id);
        if (sketchOptional.isPresent()) {
            return sketchOptional.get();
        }
        return null;
    }

    @Override
    public byte[] getImageByAlgorithmAndSketch(UUID algoId, UUID sketchId) {
        final Image image = this.imageRepository.findImageBySketchId(sketchId);
        return image.getImage();
    }

    @Override
    public Sketch getSketchByAlgorithmAndSketch(UUID algoId, UUID sketchId) {
        return this.getSketchByAlgoIdAndSketchId(algoId, sketchId);
    }


    private Sketch getSketchByAlgoIdAndSketchId(UUID algoId, UUID sketchId) {
        final Optional<Algorithm> algorithmOptional = this.algorithmRepository.findById(algoId);
        Algorithm persistedAlgorithm = null;
        if (algorithmOptional.isPresent()) {
            persistedAlgorithm = algorithmOptional.get();
        }
        Sketch sketch = new Sketch();

        for (Sketch s : persistedAlgorithm.getSketches()) {
            if (s.getId().compareTo(sketchId) == 0) {
                sketch = s;
            }
        }
        return sketch;
    }

}
