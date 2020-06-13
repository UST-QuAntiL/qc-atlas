package org.planqk.atlas.core.services;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.planqk.atlas.core.model.Algorithm;
import org.planqk.atlas.core.model.ClassicAlgorithm;
import org.planqk.atlas.core.model.ComputationModel;
import org.planqk.atlas.core.model.Publication;
import org.planqk.atlas.core.util.AtlasDatabaseTestBase;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class PublicationServiceTest extends AtlasDatabaseTestBase {

    @Autowired
    private PublicationService publicationService;
    @Autowired
    private AlgorithmService algorithmService;

    @Test
    void testAddPublication() throws MalformedURLException {
        Publication publication = getGenericTestPublication("testPublicationTitle");

        Publication storedPublication = publicationService.save(publication);
        assertPublicationEquality(storedPublication, publication);
    }

    @Test
    void testUpdatePublication_ElementNotFound() {
        Assertions.assertThrows(NoSuchElementException.class, () ->
            publicationService.update(UUID.randomUUID(), null));
    }

    @Test
    void testUpdatePublication_ElementFound() throws MalformedURLException {
        Publication publication = getGenericTestPublication("testPublicationTitle");
        Publication comparePublication = getGenericTestPublication("testPublicationTitle");

        Publication storedPublication = publicationService.save(publication);
        comparePublication.setId(storedPublication.getId());
        storedPublication.setTitle("editedPublicationTitle");
        Publication editedProblemType = publicationService.update(storedPublication.getId(), storedPublication);

        assertThat(editedProblemType.getId()).isNotNull();
        assertThat(editedProblemType.getId()).isEqualTo(comparePublication.getId());
        assertThat(editedProblemType.getTitle()).isNotEqualTo(comparePublication.getTitle());
        assertThat(storedPublication.getUrl()).isEqualTo(comparePublication.getUrl());
        assertThat(storedPublication.getDoi()).isEqualTo(comparePublication.getDoi());
        assertThat(storedPublication.getAuthors().stream().filter(author ->
                comparePublication.getAuthors().contains(author))
                .count()).isEqualTo(comparePublication.getAuthors().size());
    }

    @Test
    void testFindPublicationById_ElementNotFound() {
        Assertions.assertThrows(NoSuchElementException.class, () ->
                publicationService.findById(UUID.randomUUID()));
    }

    @Test
    void testFindPublicationById_ElementFound() throws MalformedURLException {
        Publication publication = getGenericTestPublication("testPublicationTitle");
        Publication storedPublication = publicationService.save(publication);

        storedPublication = publicationService.findById(storedPublication.getId());

        assertPublicationEquality(storedPublication, publication);
    }

    @Test
    void testFindPublicationAlgorithms() throws MalformedURLException {
        Algorithm algorithm = new ClassicAlgorithm();
        algorithm.setName("testName");
        algorithm.setComputationModel(ComputationModel.CLASSIC);

        Set<Publication> publications = new HashSet<>();
        Publication publication = getGenericTestPublication("testPublicationTitle");
        Publication storedPublication = publicationService.save(publication);
        publications.add(publication);

        algorithm.setPublications(publications);

        Algorithm storedAlgorithm = algorithmService.save(algorithm);

        Set<Algorithm> publicationAlgorithms = publicationService.findPublicationAlgorithms(storedPublication.getId());

        publicationAlgorithms.forEach(algo -> {
            assertThat(algo.getId()).isEqualTo(storedAlgorithm.getId());
        });
    }

    @Test
    void testDeletePublication() throws MalformedURLException {
        Publication publication = getGenericTestPublication("testPublicationTitle");
        Publication storedPublication = publicationService.save(publication);

        Assertions.assertDoesNotThrow(() -> publicationService.findById(storedPublication.getId()));

        publicationService.deleteById(storedPublication.getId());

        Assertions.assertThrows(NoSuchElementException.class, () ->
                publicationService.findById(storedPublication.getId()));
    }

    @Test
    void testDeletePublications() throws MalformedURLException {
        Publication publication1 = getGenericTestPublication("testPublicationTitle1");
        Publication storedPublication1 = publicationService.save(publication1);
        Publication publication2 = getGenericTestPublication("testPublicationTitle2");
        Publication storedPublication2 = publicationService.save(publication2);

        Set<UUID> publicationIds = new HashSet<>();
        publicationIds.add(storedPublication1.getId());
        publicationIds.add(storedPublication2.getId());

        Assertions.assertDoesNotThrow(() -> publicationService.findById(storedPublication1.getId()));
        Assertions.assertDoesNotThrow(() -> publicationService.findById(storedPublication2.getId()));

        publicationService.deletePublicationsByIds(publicationIds);

        Assertions.assertThrows(NoSuchElementException.class, () ->
                publicationService.findById(storedPublication1.getId()));
        Assertions.assertThrows(NoSuchElementException.class, () ->
                publicationService.findById(storedPublication2.getId()));
    }

    private void assertPublicationEquality(Publication dbPublication, Publication comparePublication) {
        assertThat(dbPublication.getId()).isNotNull();
        assertThat(dbPublication.getTitle()).isEqualTo(comparePublication.getTitle());
        assertThat(dbPublication.getUrl()).isEqualTo(comparePublication.getUrl());
        assertThat(dbPublication.getDoi()).isEqualTo(comparePublication.getDoi());
        assertThat(dbPublication.getAuthors().stream().filter(author ->
                comparePublication.getAuthors().contains(author))
                .count()).isEqualTo(comparePublication.getAuthors().size());
    }

    private Publication getGenericTestPublication(String title) throws MalformedURLException {
        Publication publication = new Publication();
        publication.setTitle(title);
        publication.setUrl(new URL("http://example.com"));
        publication.setDoi("testDoi");
        List<String> publicationAuthors = new ArrayList<>();
        publicationAuthors.add("test publication author");
        publication.setAuthors(publicationAuthors);
        return publication;
    }

}
