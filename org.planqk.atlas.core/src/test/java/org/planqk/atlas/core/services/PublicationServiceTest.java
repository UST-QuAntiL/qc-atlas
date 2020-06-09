package org.planqk.atlas.core.services;

import org.junit.jupiter.api.Test;
import org.planqk.atlas.core.repository.AlgorithmRepository;
import org.planqk.atlas.core.repository.PublicationRepository;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.springframework.beans.factory.annotation.Autowired;

public class PublicationServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private PublicationRepository publicationRepository;
    @Autowired
    private AlgorithmRepository algorithmRepository;

    @Test
    void testAddPublication() {

    }

    @Test
    void testUpdatePublication_ElementNotFound() {

    }

    @Test
    void testUpdatePublication_ElementFound() {

    }

    @Test
    void testFindPublicationById_ElementNotFound() {

    }

    @Test
    void testFindPublicationById_ElementFound() {

    }

    @Test
    void testFindPublicationAlgorithms() {

    }

    @Test
    void testDeletePublication() {

    }

    @Test
    void testDeletePublications() {

    }

}
