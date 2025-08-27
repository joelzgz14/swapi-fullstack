package com.example.swapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Person extends BaseEntity {
  private String height;
  private String mass;
  private String hair_color;
  private String skin_color;
  private String eye_color;
  private String birth_year;
  private String gender;
  private String url;

  public String getUrl() { return url; }
  public void setUrl(String url) { this.url = url; }
  // getters and setters omitted for brevity
}
