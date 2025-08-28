
package com.example.swapi.controller;

import com.example.swapi.dto.PageResponse;
import com.example.swapi.model.Person;
import com.example.swapi.service.EntityQueryService;
import com.example.swapi.sort.SortDirection;
import com.example.swapi.sort.SortKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PeopleControllerTest {

    @Mock
    private EntityQueryService queryService;

    @InjectMocks
    private PeopleController controller;

    private PageResponse<Person> mockPageResponse;

    @BeforeEach
    void setUp() {
        Person mockPerson = new Person();
        mockPerson.setName("Luke Skywalker");
        mockPerson.setCreated(OffsetDateTime.now());

        mockPageResponse = new PageResponse<>(
                List.of(mockPerson),
                1, 15, 1, 1,
                "name", "asc", ""
        );
    }

    @Test
    void testGetPeopleWithDefaultParams() {
        when(queryService.getEntities(eq(Person.class), eq(1), eq(15), eq(""),
                eq(SortKey.name), eq(SortDirection.asc)))
                .thenReturn(Mono.just(mockPageResponse));

        StepVerifier.create(controller.getPeople(1, 15, "", SortKey.name, SortDirection.asc))
                .expectNextMatches(response ->
                        response.getContent().size() == 1 &&
                                response.getContent().get(0).getName().equals("Luke Skywalker") &&
                                response.getPage() == 1 &&
                                response.getSize() == 15)
                .verifyComplete();
    }

    @Test
    void testGetPeopleWithCustomPagination() {
        PageResponse<Person> customPageResponse = new PageResponse<>(
                mockPageResponse.getContent(), 2, 10,
                mockPageResponse.getTotalElements(),
                mockPageResponse.getTotalPages(),
                mockPageResponse.getSort(),
                mockPageResponse.getDirection(),
                mockPageResponse.getSearch()
        );

        when(queryService.getEntities(eq(Person.class), eq(2), eq(10), eq(""),
                eq(SortKey.name), eq(SortDirection.asc)))
                .thenReturn(Mono.just(customPageResponse));

        StepVerifier.create(controller.getPeople(2, 10, "", SortKey.name, SortDirection.asc))
                .expectNextMatches(response ->
                        response.getPage() == 2 &&
                                response.getSize() == 10)
                .verifyComplete();
    }

    @Test
    void testGetPeopleWithSearchParam() {
        PageResponse<Person> searchResponse = new PageResponse<>(
                mockPageResponse.getContent(),
                mockPageResponse.getPage(),
                mockPageResponse.getSize(),
                mockPageResponse.getTotalElements(),
                mockPageResponse.getTotalPages(),
                mockPageResponse.getSort(),
                mockPageResponse.getDirection(),
                "Luke"
        );

        when(queryService.getEntities(eq(Person.class), eq(1), eq(15), eq("Luke"),
                eq(SortKey.name), eq(SortDirection.asc)))
                .thenReturn(Mono.just(searchResponse));

        StepVerifier.create(controller.getPeople(1, 15, "Luke", SortKey.name, SortDirection.asc))
                .expectNextMatches(response -> response.getSearch().equals("Luke"))
                .verifyComplete();
    }

    @Test
    void testGetPeopleWithCustomSorting() {
        PageResponse<Person> sortResponse = new PageResponse<>(
                mockPageResponse.getContent(),
                mockPageResponse.getPage(),
                mockPageResponse.getSize(),
                mockPageResponse.getTotalElements(),
                mockPageResponse.getTotalPages(),
                "created",
                "desc",
                mockPageResponse.getSearch()
        );

        when(queryService.getEntities(eq(Person.class), eq(1), eq(15), eq(""),
                eq(SortKey.created), eq(SortDirection.desc)))
                .thenReturn(Mono.just(sortResponse));

        StepVerifier.create(controller.getPeople(1, 15, "", SortKey.created, SortDirection.desc))
                .expectNextMatches(response ->
                        response.getSort().equals("created") &&
                                response.getDirection().equals("desc"))
                .verifyComplete();
    }

    @Test
    void testGetPeopleHandlesError() {
        when(queryService.getEntities(eq(Person.class), eq(1), eq(15), eq(""),
                eq(SortKey.name), eq(SortDirection.asc)))
                .thenReturn(Mono.error(new RuntimeException("Service error")));

        StepVerifier.create(controller.getPeople(1, 15, "", SortKey.name, SortDirection.asc))
                .expectError(RuntimeException.class)
                .verify();
    }
}