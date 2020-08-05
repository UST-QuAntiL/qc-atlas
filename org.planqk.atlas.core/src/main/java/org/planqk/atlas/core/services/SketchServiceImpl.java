package org.planqk.atlas.core.services;

import java.util.Base64;
import java.util.UUID;


import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.Sketch;
import org.planqk.atlas.core.repository.SketchRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SketchServiceImpl implements SketchService {

    private final SketchRepository sketchRepository;
    private final AlgorithmService algorithmService;

    @Override
    public Sketch addSketchToAlgorithm(UUID algorithmId, MultipartFile file, String description) {
        try {
            byte[] fileContent = file.getBytes();
            String base64Image = "data:" + file.getContentType() + ";base64," + Base64.getEncoder().encodeToString(fileContent);
            Sketch sketch = new Sketch();
            sketch.setImage(base64Image);
            sketch.setDescription(description);
            Algorithm algorithm = algorithmService.findById(algorithmId);
            sketch.setAlgorithm(algorithm);
            sketchRepository.save(sketch);
            return sketch;
        } catch (Exception e) {
            throw new RuntimeException("Could not store the Sketch. Error: " + e.getMessage());
        }
    }

    @Override
    public void delete(UUID sketchId){
        sketchRepository.deleteById(sketchId);
    }
}
