package com.example.swapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Planet extends BaseEntity {
  private String diameter;
  private String climate;
  private String gravity;
  private String terrain;
  private String url;

  public String getUrl() { return url; }
  public void setUrl(String url) { this.url = url; }
  // getters and setters omitted for brevity
}
