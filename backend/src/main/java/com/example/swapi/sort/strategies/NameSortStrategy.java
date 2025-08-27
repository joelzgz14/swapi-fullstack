package com.example.swapi.sort.strategies;

import com.example.swapi.model.BaseEntity;
import com.example.swapi.sort.SortDirection;
import com.example.swapi.sort.SortKey;
import com.example.swapi.sort.SortStrategy;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class NameSortStrategy<T extends BaseEntity> implements SortStrategy<T> {

    @Override
    public SortKey getSortKey() {
        return SortKey.name;
    }

    @Override
    public Comparator<T> getComparator(SortDirection direction) {
        Comparator<T> comparator = Comparator.comparing(
                T::getName,
                Comparator.nullsLast(String::compareToIgnoreCase)
        );
        return direction == SortDirection.desc ? comparator.reversed() : comparator;
    }
}