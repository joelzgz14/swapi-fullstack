// EntityService.java
package com.example.swapi.service;

import com.example.swapi.model.BaseEntity;
import com.example.swapi.sort.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EntityService {

    public <T extends BaseEntity> List<T> sortEntities(List<T> entities, SortKey sortKey, SortDirection direction) {
        SortStrategyFactory<T> factory = new SortStrategyFactory<>();
        SortStrategy<T> strategy = factory.getStrategy(sortKey);

        return entities.stream()
                .sorted(strategy.getComparator(direction))
                .collect(Collectors.toList());
    }
}