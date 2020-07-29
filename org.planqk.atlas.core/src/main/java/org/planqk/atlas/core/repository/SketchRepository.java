package org.planqk.atlas.core.repository;

import java.util.UUID;

import org.planqk.atlas.core.model.Sketch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repository to access {@link Sketch}s.
 */
@RepositoryRestResource(exported = false)
public interface SketchRepository extends JpaRepository<Sketch, UUID> {

}
