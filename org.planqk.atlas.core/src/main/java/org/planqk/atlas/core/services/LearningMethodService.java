package org.planqk.atlas.core.services;

import java.util.UUID;

import org.planqk.atlas.core.exceptions.EntityReferenceConstraintViolationException;
import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.LearningMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service class for operations related to interacting and modifying {@link LearningMethod}s in the database.
 */
public interface LearningMethodService {
    /**
     * Creates a new database entry for a given {@link LearningMethod} and save it to the database.
     * <p>
     * The ID of the {@link LearningMethod} parameter should be null, since the ID will be generated by the
     * database when creating the entry. The validation for this is done by the Controller layer, which will reject
     * {@link LearningMethod}s with a given ID in its create path.
     *
     * @param learningMethod The {@link LearningMethod} object describing a type of learning that could be solved
     *                       by an {@link Algorithm}
     * @return The {@link LearningMethod} object that represents the saved status from the database
     */
    @Transactional
    LearningMethod create(LearningMethod learningMethod);

    /**
     * Retrieve multiple {@link LearningMethod} entries from the database.
     * <p>
     * The amount of entries is based on the given {@link Pageable} parameter. If the {@link Pageable} is unpaged a {@link Page} with all entries is
     * queried.
     * <p>
     * If no search should be executed the search parameter can be left null or empty.
     *
     * @param pageable The page information, namely page size and page number, of the page we want to retrieve
     * @param search   The string based on which a search will be executed
     * @return The page of queried {@link LearningMethod} entries
     */
    Page<LearningMethod> findAll(Pageable pageable, String search);

    /**
     * Find a database entry of a {@link LearningMethod} that is already saved in the database. This search is based on the ID the database has given
     * the {@link LearningMethod} object when it was created and first saved to the database.
     * <p>
     * If there is no entry found in the database this method will throw a {@link java.util.NoSuchElementException}.
     *
     * @param learningMethodId The ID of the {@link LearningMethod} we want to find
     * @return The {@link LearningMethod} with the given ID
     */
    LearningMethod findById(UUID learningMethodId);

    /**
     * Update an existing {@link LearningMethod} database entry by saving the updated {@link LearningMethod} object to the the database.
     * <p>
     * The ID of the {@link LearningMethod} parameter has to be set to the ID of the database entry we want to update. The validation for this ID to
     * be set is done by the Controller layer, which will reject {@link LearningMethod}s without a given ID in its update path. This ID will be used
     * to query the existing {@link LearningMethod} entry we want to update. If no {@link LearningMethod} entry with the given ID is found this
     * method will throw a {@link java.util.NoSuchElementException}.
     *
     * @param learningMethod The {@link LearningMethod} we want to update with its updated properties
     * @return the updated {@link LearningMethod} object that represents the updated status of the database
     */
    @Transactional
    LearningMethod update(LearningMethod learningMethod);

    /**
     * Delete an existing {@link LearningMethod} entry from the database. This deletion is based on the ID the database has given
     * the {@link LearningMethod} when it was created and first saved to the database.
     * <p>
     * All {@link LearningMethod}s where the deleted {@link LearningMethod} is a parent, will be have their parent problem type set to null.
     * <p>
     * If no entry with the given ID is found this method will throw a {@link java.util.NoSuchElementException}.
     * <p>
     * If the {@link LearningMethod} is still referenced by at least one {@link Algorithm} a {@link
     * EntityReferenceConstraintViolationException} will be thrown.
     *
     * @param learningMethodId The ID of the {@link LearningMethod} we want to delete
     */
    @Transactional
    void delete(UUID learningMethodId);
}
