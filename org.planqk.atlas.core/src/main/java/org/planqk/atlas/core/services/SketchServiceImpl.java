package org.planqk.atlas.core.services;

import java.sql.Blob;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;


import javax.servlet.http.HttpServletRequest;
import javax.sql.rowset.serial.SerialBlob;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.Image;
import org.planqk.atlas.core.model.Sketch;
import org.planqk.atlas.core.repository.ImageRepository;
import org.planqk.atlas.core.repository.SketchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SketchServiceImpl implements SketchService {

    private final SketchRepository sketchRepository;
    private final AlgorithmService algorithmService;
    private ImageRepository imageRepository;

    @Override
    public Sketch addSketchToAlgorithm(UUID algorithmId, MultipartFile file, String description, String baseURL) {
        try {
            // TODO change me to save as 'imageURL'
//            imageURL = "htttp:ip:/api/algorithms/${}/sketches/${}"
            byte[] fileContent = file.getBytes();


            final Image image = new Image();

            image.setImage(fileContent);
            imageRepository.save(image);

            Sketch sketch = new Sketch();
            sketch.setDescription(description);
            Algorithm algorithm = algorithmService.findById(algorithmId);
            sketch.setAlgorithm(algorithm);
            Sketch persistedSketch = sketchRepository.save(sketch);
            persistedSketch.setImageURL(baseURL + "/algorithms/" + algorithmId  + "/sketches/" + persistedSketch.getId());

            sketchRepository.save(persistedSketch);
            return sketch;
        } catch (Exception e) {
            throw new RuntimeException("Could not store the Sketch. Error: " + e.getMessage());
        }
    }

    @Override
    public void delete(UUID sketchId){
        sketchRepository.deleteById(sketchId);
    }

    @Override
    public Sketch findById(UUID id) {
        final Optional<Sketch> sketchOptional = this.sketchRepository.findById(id);
        if(sketchOptional.isPresent()){
            return sketchOptional.get();
        }
        return null;
    }
}
