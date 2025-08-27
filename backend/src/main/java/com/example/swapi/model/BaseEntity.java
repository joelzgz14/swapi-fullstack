package com.example.swapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BaseEntity {
  private String name;
  private OffsetDateTime created;

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public OffsetDateTime getCreated() { return created; }
  public void setCreated(OffsetDateTime created) { this.created = created; }
}
