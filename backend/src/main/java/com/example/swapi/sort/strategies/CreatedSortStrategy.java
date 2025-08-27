// CreatedSortStrategy.java

package com.example.swapi.sort.strategies;

import com.example.swapi.model.BaseEntity;
import com.example.swapi.sort.SortDirection;
import com.example.swapi.sort.SortKey;
import com.example.swapi.sort.SortStrategy;

import java.util.Comparator;

public class CreatedSortStrategy<T extends BaseEntity> implements SortStrategy<T> {

    @Override
    public SortKey getSortKey() {
        return SortKey.created;
    }

    @Override
    public Comparator<T> getComparator(SortDirection direction) {
        Comparator<T> comparator = Comparator.comparing(entity -> entity.getCreated());
        return direction == SortDirection.desc ? comparator.reversed() : comparator;
    }
}