package org.planqk.atlas.core.repository;

import java.util.Optional;
import java.util.UUID;

import org.planqk.atlas.core.model.File;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, UUID> {

    Optional<File> findByFileURL(String fileURL);

    @Query(value = "SELECT * " +
        "FROM file " +
        "INNER JOIN implementation_files on file.id = implementation_files.file_id " +
        "INNER JOIN knowledge_artifact ka on file.id = ka.id " +
        "WHERE implementation_files.implementation_id = :implId",
        nativeQuery = true)
    Page<File> findFilesByImplementation(@Param("implId") UUID implementationId, Pageable pageable);
}

