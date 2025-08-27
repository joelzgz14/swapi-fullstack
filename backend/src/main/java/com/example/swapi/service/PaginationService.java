package com.example.swapi.service;

import com.example.swapi.dto.PageResponse;
import com.example.swapi.dto.SwapiPage;
import com.example.swapi.model.BaseEntity;
import com.example.swapi.model.Person;
import com.example.swapi.model.Planet;
import com.example.swapi.sort.CommonSortStrategies;
import com.example.swapi.sort.SortDirection;
import com.example.swapi.sort.SortKey;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PaginationService {

  private final SwapiClient swapiClient;
  private final CommonSortStrategies<BaseEntity> sortStrategies;

  public PaginationService(SwapiClient swapiClient, CommonSortStrategies<BaseEntity> strategies) {
    this.swapiClient = swapiClient;
    this.sortStrategies = strategies;
  }

  public Mono<PageResponse<Person>> people(int page, int size, String search, SortKey sort, SortDirection dir) {
    return fetchAggregate(page, size, search, sort, dir, EntityType.PEOPLE);
  }

  public Mono<PageResponse<Planet>> planets(int page, int size, String search, SortKey sort, SortDirection dir) {
    return fetchAggregate(page, size, search, sort, dir, EntityType.PLANETS);
  }

  @SuppressWarnings("unchecked")
  private <T extends BaseEntity> Mono<PageResponse<T>> fetchAggregate(int page, int size, String search, SortKey sort, SortDirection dir, EntityType type) {
    // fetch all SWAPI pages sequentially until exhausted, then process
    List<T> all = new ArrayList<>();
    return fetchPageRecursive(1, type, (List<BaseEntity>) all)
        .map(list -> applySearchSortAndPage(list, page, size, search, sort, dir));
  }

  private enum EntityType { PEOPLE, PLANETS }

  private Mono<List<? extends BaseEntity>> fetchPageRecursive(int swapiPage, EntityType type, List<BaseEntity> accumulator) {
    Mono<? extends SwapiPage<? extends BaseEntity>> mono =
        (type == EntityType.PEOPLE) ? swapiClient.fetchPeoplePage(swapiPage) : swapiClient.fetchPlanetsPage(swapiPage);

    return mono.flatMap(sp -> {
      List<? extends BaseEntity> results = sp.getResults() == null ? List.of() : sp.getResults();
      accumulator.addAll(results);
      if (sp.getNext() != null && !sp.getNext().isBlank()) {
        return fetchPageRecursive(swapiPage + 1, type, accumulator);
      } else {
        return Mono.just(accumulator);
      }
    });
  }

  @SuppressWarnings("unchecked")
  private <T extends BaseEntity> PageResponse<T> applySearchSortAndPage(List<? extends BaseEntity> list, int page, int size, String search, SortKey sort, SortDirection dir) {
    Stream<T> stream = (Stream<T>) list.stream();

    if (StringUtils.hasText(search)) {
      String needle = search.toLowerCase(Locale.ROOT);
      stream = stream.filter(e -> e.getName() != null && e.getName().toLowerCase(Locale.ROOT).contains(needle));
    }

    List<T> filtered = stream.collect(Collectors.toList());
    filtered.sort(sortStrategies.getComparator(sort, dir));

    int total = filtered.size();
    int totalPages = (int) Math.ceil(total / (double) size);
    int from = Math.max(0, Math.min((page - 1) * size, Math.max(0, total - 1)));
    int to = Math.min(from + size, total);
    List<T> content = total == 0 ? List.of() : filtered.subList(from, to);

    return new PageResponse<>(content, page, size, total, totalPages, sort.name(), dir.name(), search);
  }
}
