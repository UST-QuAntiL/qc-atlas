package org.planqk.atlas.core.services;

import java.util.List;
import java.util.UUID;

import org.planqk.atlas.core.model.Image;
import org.planqk.atlas.core.model.Sketch;

import org.springframework.web.multipart.MultipartFile;

/**
 * Service class for operations related to interacting and modifying {@link Sketch}es in the database.
 */
public interface SketchService {

    Sketch update(Sketch sketch);

    List<Sketch> findByAlgorithm(UUID algorithmId);

    Sketch addSketchToAlgorithm(UUID algorithmId, MultipartFile file, String description, String baseURL);

    void delete(UUID sketchId);

    Sketch findById(UUID sketchId);

    Image getImageBySketch(final UUID sketchId);


}
