package org.planqk.atlas.core.services;

import java.util.List;
import java.util.UUID;

import org.planqk.atlas.core.model.Sketch;
import org.springframework.web.multipart.MultipartFile;

public interface SketchService {

    public Sketch update(UUID id, Sketch sketch);

    public List<Sketch> findByAlgorithm(UUID algorithmId);

    public Sketch addSketchToAlgorithm(UUID algorithmId, MultipartFile file, String description, String baseURL);

    public void delete(UUID sketchId);

    public Sketch findById(UUID id);

    byte[] getImageBySketch(final UUID sketchId);
}
