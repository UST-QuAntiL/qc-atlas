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

    /**
     * Update an existing {@link Sketch} database entry by saving the updated {@link Sketch} object
     * to the the database.
     * <p>
     * The ID of the {@link Sketch} parameter has to be set to the ID of the database entry we want to update.
     * The validation for this ID to be set is done by the Controller layer, which will reject {@link Sketch}s
     * without a given ID in its update path.
     * This ID will be used to query the existing {@link Sketch} entry we want to update.
     * If no {@link Sketch} entry with the given ID is found this method will throw a
     * {@link java.util.NoSuchElementException}.
     *
     * @param sketch The {@link Sketch} we want to update with its updated properties
     * @return the updated {@link Sketch} object that represents the updated status of the database
     */
    Sketch update(Sketch sketch);

    /**
     * Retrieve multiple {@link Sketch}s entries from the database based on if they are sketches of the given
     * {@link org.planqk.atlas.core.model.Algorithm}.
     * If no entries are found an empty page is returned.
     *
     * @param algorithmId The ID of the {@link org.planqk.atlas.core.model.Algorithm} we want find linked {@link Sketch}s for
     * @return The list of queried {@link Sketch} entries which are linked to given the {@link org.planqk.atlas.core.model.Algorithm}
     */
    List<Sketch> findByAlgorithm(UUID algorithmId);

    /**
     * Add a {@link Sketch} to an already existing {@link org.planqk.atlas.core.model.Algorithm}.
     *
     * @param algorithmId The ID of the {@link org.planqk.atlas.core.model.Algorithm} we want to add a {@link Sketch} to
     * @param file The file from which the {@link Image} of the {@link Sketch} will be created
     * @param description The description of the {@link Sketch} which will be created
     * @param baseURL The base url from which the url of the {@link Sketch} which build
     * @return The created and added {@link Sketch}
     */
    Sketch addSketchToAlgorithm(UUID algorithmId, MultipartFile file, String description, String baseURL);

    /**
     * Delete an existing {@link Sketch} entry from the database.
     * This deletion is based on the ID the database has given the {@link Sketch}
     * when it was created and first saved to the database.
     *
     * @param sketchId The ID of the {@link Sketch} we want to delete
     */
    void delete(UUID sketchId);

    /**
     * Find a database entry of a {@link Sketch} that is already saved in the database.
     * This search is based on the ID the database has given the {@link Sketch}
     * object when it was created and first saved to the database.
     * <p>
     * If there is no entry found in the database this method will throw a {@link java.util.NoSuchElementException}.
     *
     * @param sketchId The ID of the {@link Sketch} we want to find
     * @return The {@link Sketch} with the given ID
     */
    Sketch findById(UUID sketchId);

    /**
     * Retrieve the image of a {@link Sketch} that is already saved in the database.
     * This search is based on the ID the database has given the {@link Sketch}
     * object when it was created and first saved to the database.
     *
     * @param sketchId The ID of the {@link Sketch} for which we want to find the image for
     * @return @return The {@link Image} of the {@link Sketch} with the given ID
     */
    Image getImageBySketch(final UUID sketchId);


}
