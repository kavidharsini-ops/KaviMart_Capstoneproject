package com.kavi.kavimart.model;

import java.util.List;

/**
 * Paginated window result with total count, current page index, page size,
 * and computed total pages.
 *
 * @param <T> the type of items contained in the page
 */
public class PageResult<T> {
  private final List<T> items;
  private final long total;
  private final int page;
  private final int pageSize;

  /**
   * Constructs a paginated result page.
   *
   * @param items list of items on current page
   * @param total total matching items count
   * @param page current 1-based page index
   * @param pageSize maximum items per page
   */
  public PageResult(List<T> items, long total, int page, int pageSize) {
    this.items = List.copyOf(items);
    this.total = total;
    this.page = page;
    this.pageSize = pageSize;
  }

  public List<T> getItems() {
    return items;
  }

  public long getTotal() {
    return total;
  }

  public int getPage() {
    return page;
  }

  public int getPageSize() {
    return pageSize;
  }

  public int getPages() {
    return (int) Math.ceil(total / (double) pageSize);
  }
}
