package com.example.swapi.sort;

import com.example.swapi.model.BaseEntity;
import java.util.Comparator;

public interface SortStrategy<T extends BaseEntity> {
    /**
     * Get the sort key this strategy handles
     */
    SortKey getSortKey();

    /**
     * Get a comparator for the specified direction
     */
    Comparator<T> getComparator(SortDirection direction);
}