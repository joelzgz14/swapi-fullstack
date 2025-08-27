package com.example.swapi.sort;

import com.example.swapi.model.BaseEntity;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;

@Component
public class CommonSortStrategies<T extends BaseEntity> {

  private final Map<SortKey, Comparator<T>> strategies = new EnumMap<>(SortKey.class);

  public CommonSortStrategies() {
    register(SortKey.name, Comparator.comparing(e -> nullSafe(e.getName()), String.CASE_INSENSITIVE_ORDER));
    register(SortKey.created, Comparator.comparing(BaseEntity::getCreated, Comparator.nullsLast(Comparator.naturalOrder())));
  }

  private void register(SortKey key, Comparator<T> comparator) {
    strategies.put(key, comparator);
  }

  public Comparator<T> getComparator(SortKey key, SortDirection direction) {
    Comparator<T> comp = strategies.getOrDefault(key, strategies.get(SortKey.name));
    if (direction == SortDirection.desc) {
      comp = comp.reversed();
    }
    return comp;
  }

  private static String nullSafe(String s) {
    return s == null ? "" : s;
  }
}
