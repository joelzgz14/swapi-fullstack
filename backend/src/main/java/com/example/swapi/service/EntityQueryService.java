package com.example.swapi.service;

import com.example.swapi.dto.PageResponse;
import com.example.swapi.dto.SwapiPage;
import com.example.swapi.model.BaseEntity;
import com.example.swapi.model.Person;
import com.example.swapi.model.Planet;
import com.example.swapi.sort.*;
import com.example.swapi.sort.strategies.CreatedSortStrategy;
import com.example.swapi.sort.strategies.NameSortStrategy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EntityQueryService {

    private final SwapiClient swapiClient;
    private final Map<Class<?>, Map<SortKey, SortStrategy<?>>> strategyMap = new HashMap<>();

    public EntityQueryService(SwapiClient swapiClient) {
        this.swapiClient = swapiClient;
        initializeSortStrategies();
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseEntity> Mono<PageResponse<T>> getEntities(
            Class<T> entityClass,
            int page,
            int size,
            String search,
            SortKey sortKey,
            SortDirection direction) {

        if (entityClass.equals(Person.class)) {
            return fetchAllPeople()
                    .map(entities -> (PageResponse<T>) processEntities(entities, page, size, search, sortKey, direction, (Class<Person>) entityClass));
        } else if (entityClass.equals(Planet.class)) {
            return fetchAllPlanets()
                    .map(entities -> (PageResponse<T>) processEntities(entities, page, size, search, sortKey, direction, (Class<Planet>) entityClass));
        }

        return Mono.error(new IllegalArgumentException("Unsupported entity type: " + entityClass.getName()));
    }

    private Mono<List<Person>> fetchAllPeople() {
        return fetchPageRecursive(1, "people")
                .map(list -> list.stream()
                        .filter(entity -> entity instanceof Person)
                        .map(entity -> (Person) entity)
                        .collect(Collectors.toList()));
    }

    private Mono<List<Planet>> fetchAllPlanets() {
        return fetchPageRecursive(1, "planets")
                .map(list -> list.stream()
                        .filter(entity -> entity instanceof Planet)
                        .map(entity -> (Planet) entity)
                        .collect(Collectors.toList()));
    }

    private Mono<List<BaseEntity>> fetchPageRecursive(int page, String entityType) {
        List<BaseEntity> accumulator = new ArrayList<>();
        return fetchPageRecursiveInternal(page, entityType, accumulator);
    }

    private Mono<List<BaseEntity>> fetchPageRecursiveInternal(int page, String entityType, List<BaseEntity> accumulator) {
        Mono<? extends SwapiPage<? extends BaseEntity>> mono;

        if ("people".equals(entityType)) {
            mono = swapiClient.fetchPeoplePage(page);
        } else {
            mono = swapiClient.fetchPlanetsPage(page);
        }

        return mono.flatMap(swapiPage -> {
            List<? extends BaseEntity> results = swapiPage.getResults() == null ? List.of() : swapiPage.getResults();
            accumulator.addAll(results);

            if (swapiPage.getNext() != null && !swapiPage.getNext().isBlank()) {
                return fetchPageRecursiveInternal(page + 1, entityType, accumulator);
            } else {
                return Mono.just(accumulator);
            }
        });
    }

    private <T extends BaseEntity> PageResponse<T> processEntities(
            List<T> entities,
            int page,
            int size,
            String search,
            SortKey sortKey,
            SortDirection direction,
            Class<T> entityClass) {

        // Filter by search term if provided
        List<T> filtered = entities;
        if (StringUtils.hasText(search)) {
            String searchLower = search.toLowerCase(Locale.ROOT);
            filtered = entities.stream()
                    .filter(entity -> entity.getName() != null &&
                            entity.getName().toLowerCase(Locale.ROOT).contains(searchLower))
                    .collect(Collectors.toList());
        }

        // Sort entities
        SortStrategy<T> strategy = getSortStrategy(entityClass, sortKey);

        List<T> sorted = filtered.stream()
                .sorted(strategy.getComparator(direction))
                .collect(Collectors.toList());

        // Apply pagination
        int total = sorted.size();
        int totalPages = (int) Math.ceil(total / (double) size);
        int from = Math.max(0, Math.min((page - 1) * size, Math.max(0, total - 1)));
        int to = Math.min(from + size, total);

        List<T> content = total == 0 ? List.of() : sorted.subList(from, to);

        return new PageResponse<>(content, page, size, total, totalPages,
                sortKey.name(), direction.name(), search);
    }

    @SuppressWarnings("unchecked")
    private <T extends BaseEntity> SortStrategy<T> getSortStrategy(Class<T> entityClass, SortKey sortKey) {
        SortStrategy<T> strategy = (SortStrategy<T>) strategyMap
                .getOrDefault(entityClass, Collections.emptyMap())
                .get(sortKey);

        if (strategy == null) {
            throw new IllegalArgumentException("No sorting strategy for " + entityClass.getSimpleName() + " and key " + sortKey);
        }
        return strategy;
    }

    private void initializeSortStrategies() {
        registerStrategies(Person.class);
        registerStrategies(Planet.class);
    }

    private <T extends BaseEntity> void registerStrategies(Class<T> entityClass) {
        Map<SortKey, SortStrategy<?>> strategies = new HashMap<>();
        strategies.put(SortKey.name, new NameSortStrategy<>());
        strategies.put(SortKey.created, new CreatedSortStrategy<>());

        strategyMap.put(entityClass, strategies);
    }
}