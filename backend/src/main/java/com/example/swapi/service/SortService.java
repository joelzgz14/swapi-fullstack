// SortService.java
package com.example.swapi.service;

import com.example.swapi.model.BaseEntity;
import com.example.swapi.sort.SortDirection;
import com.example.swapi.sort.SortKey;
import com.example.swapi.sort.SortStrategy;
import com.example.swapi.model.Person;
import com.example.swapi.model.Planet;
import com.example.swapi.sort.strategies.CreatedSortStrategy;
import com.example.swapi.sort.strategies.NameSortStrategy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SortService {
    private final Map<Class<?>, Map<SortKey, SortStrategy<?>>> strategyMap = new HashMap<>();

    public SortService() {
        initializeStrategies();
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseEntity> List<T> sort(List<T> items, Class<T> type, SortKey key, SortDirection direction) {
        if (items == null || items.isEmpty()) {
            return items;
        }

        Map<SortKey, SortStrategy<?>> strategies = strategyMap.get(type);
        if (strategies == null) {
            throw new IllegalArgumentException("No sorting strategies registered for type: " + type.getName());
        }

        SortStrategy<T> strategy = (SortStrategy<T>) strategies.get(key);
        if (strategy == null) {
            throw new IllegalArgumentException("No sorting strategy found for key: " + key);
        }

        return items.stream()
                .sorted(strategy.getComparator(direction))
                .collect(Collectors.toList());
    }

    private void initializeStrategies() {
        // Register strategies for Person
        registerStrategies(Person.class,
                Person::getName,
                Person::getCreated);

        registerStrategies(Planet.class,
                Planet::getName,
                Planet::getCreated);
    }

    private <T> void registerStrategies(
            Class<T> type,
            Function<T, String> nameExtractor,
            Function<T, OffsetDateTime> createdExtractor) {

        Map<SortKey, SortStrategy<?>> strategies = new HashMap<>();
        strategies.put(SortKey.name, new NameSortStrategy<>());
        strategies.put(SortKey.created, new CreatedSortStrategy<>());

        strategyMap.put(type, strategies);
    }
}