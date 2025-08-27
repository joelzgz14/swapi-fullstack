package com.example.swapi.dto;

import java.util.List;

public class PageResponse<T> {
  private List<T> content;
  private int page;
  private int size;
  private long totalElements;
  private int totalPages;
  private String sort;
  private String direction;
  private String search;

  public PageResponse() {}

  public PageResponse(List<T> content, int page, int size, long totalElements, int totalPages, String sort, String direction, String search) {
    this.content = content;
    this.page = page;
    this.size = size;
    this.totalElements = totalElements;
    this.totalPages = totalPages;
    this.sort = sort;
    this.direction = direction;
    this.search = search;
  }

  public List<T> getContent() { return content; }
  public void setContent(List<T> content) { this.content = content; }
  public int getPage() { return page; }
  public void setPage(int page) { this.page = page; }
  public int getSize() { return size; }
  public void setSize(int size) { this.size = size; }
  public long getTotalElements() { return totalElements; }
  public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
  public int getTotalPages() { return totalPages; }
  public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
  public String getSort() { return sort; }
  public void setSort(String sort) { this.sort = sort; }
  public String getDirection() { return direction; }
  public void setDirection(String direction) { this.direction = direction; }
  public String getSearch() { return search; }
  public void setSearch(String search) { this.search = search; }
}
