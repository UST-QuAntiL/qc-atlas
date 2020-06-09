package org.planqk.atlas.core.repository;

import org.planqk.atlas.core.model.Publication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Repository to access {@link Publication}s available in the data base with different queries.
 */
@Repository
@RepositoryRestResource(exported = false)
public interface PublicationRepository extends JpaRepository<Publication, UUID> {

    Optional<Publication> findByTitle(String title);

    @Query("DELETE FROM Publication pub WHERE pub.id IN :publicationIds")
    void deletePublicationsByIds(@Param("publicationIds") Set<UUID> publicationIds);
}
