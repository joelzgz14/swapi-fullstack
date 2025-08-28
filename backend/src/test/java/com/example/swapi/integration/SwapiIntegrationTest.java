package com.example.swapi.integration;

import com.example.swapi.dto.PageResponse;
import com.example.swapi.model.Person;
import com.example.swapi.model.Planet;
import com.example.swapi.sort.SortDirection;
import com.example.swapi.sort.SortKey;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class SwapiIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testGetPlanetsEndpoint() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/planets")
                        .queryParam("page", 1)
                        .queryParam("size", 10)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<PageResponse<Planet>>() {})
                .value(response -> {
                    assert response.getContent().size() > 0;
                    Planet planet = response.getContent().get(0);
                    assert planet.getName() != null;
                    assert planet.getCreated() != null;
                    assert planet.getDiameter() != null;
                    assert planet.getClimate() != null;
                });
    }

    @Test
    void testGetPeopleEndpoint() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/people")
                        .queryParam("page", 1)
                        .queryParam("size", 10)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<PageResponse<Person>>() {})
                .value(response -> {
                    assert response.getContent().size() > 0;
                    Person person = response.getContent().get(0);
                    assert person.getName() != null;
                    assert person.getCreated() != null;
                    assert person.getGender() != null;
                    assert person.getBirth_year() != null;
                });
    }

    @Test
    void testPlanetSearchFunctionality() {
        String searchTerm = "Tatooine";
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/planets")
                        .queryParam("search", searchTerm)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<PageResponse<Planet>>() {})
                .value(response -> {
                    assert response.getSearch().equals(searchTerm);
                    assert response.getContent().stream()
                            .map(Planet::getName)
                            .anyMatch(name -> name.contains(searchTerm));
                });
    }

    @Test
    void testSortingFunctionality() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/people")
                        .queryParam("sort", SortKey.name)
                        .queryParam("direction", SortDirection.desc)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<PageResponse<Person>>() {})
                .value(response -> {
                    assert response.getSort().equals("name");
                    assert response.getDirection().equals("desc");
                });
    }
}
