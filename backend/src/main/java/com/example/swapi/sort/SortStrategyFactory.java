package com.example.swapi.sort;

import com.example.swapi.model.BaseEntity;
import com.example.swapi.sort.strategies.NameSortStrategy;
import com.example.swapi.sort.strategies.CreatedSortStrategy;

import java.util.HashMap;
import java.util.Map;

public class SortStrategyFactory<T extends BaseEntity> {

    private final Map<SortKey, SortStrategy<T>> strategies = new HashMap<>();

    public SortStrategyFactory() {
        registerStrategy(new NameSortStrategy<>());
        registerStrategy(new CreatedSortStrategy<>());
    }

    private void registerStrategy(SortStrategy<T> strategy) {
        strategies.put(strategy.getSortKey(), strategy);
    }

    public SortStrategy<T> getStrategy(SortKey key) {
        return strategies.get(key);
    }
}