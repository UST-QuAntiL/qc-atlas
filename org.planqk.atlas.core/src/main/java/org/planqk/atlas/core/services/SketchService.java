package org.planqk.atlas.core.services;

import java.util.UUID;

import org.planqk.atlas.core.model.Sketch;
import org.springframework.web.multipart.MultipartFile;

public interface SketchService {

    public Sketch addSketchToAlgorithm(UUID algorithmId, MultipartFile file, String description);

    public void delete(UUID sketchId);
}
