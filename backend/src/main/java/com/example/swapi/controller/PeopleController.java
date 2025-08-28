package com.example.swapi.controller;

import com.example.swapi.dto.PageResponse;
import com.example.swapi.model.Person;
import com.example.swapi.service.EntityQueryService;
import com.example.swapi.service.PaginationService;
import com.example.swapi.sort.SortDirection;
import com.example.swapi.sort.SortKey;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/people")
@Validated
public class PeopleController {

    private final EntityQueryService queryService;

  public PeopleController(EntityQueryService queryService) {
    this.queryService = queryService;
  }

  @GetMapping
  public Mono<PageResponse<Person>> getPeople(
      @RequestParam(defaultValue = "1") @Min(1) int page,
      @RequestParam(defaultValue = "15") @Min(1) int size,
      @RequestParam(defaultValue = "") String search,
      @RequestParam(defaultValue = "name") SortKey sort,
      @RequestParam(defaultValue = "desc") SortDirection dir
  ) {
      return queryService.getEntities(Person.class, page, size, search, sort, dir);

  }
}
