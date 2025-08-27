package com.example.swapi.controller;

import com.example.swapi.dto.PageResponse;
import com.example.swapi.model.Planet;
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
@RequestMapping("/planets")
@Validated
public class PlanetsController {

    private final EntityQueryService queryService;

  public PlanetsController(EntityQueryService queryService) {
    this.queryService = queryService;
  }

  @GetMapping
  public Mono<PageResponse<Planet>> getPlanets(
      @RequestParam(defaultValue = "1") @Min(1) int page,
      @RequestParam(defaultValue = "15") @Min(1) int size,
      @RequestParam(defaultValue = "") String search,
      @RequestParam(defaultValue = "name") SortKey sort,
      @RequestParam(defaultValue = "asc") SortDirection dir
  ) {
      return queryService.getEntities(Planet.class, page, size, search, sort, dir);

  }
}
