package com.kavi.kavimart.model;
import java.util.List;
/** Page window with total row count and computed number of pages. */
public class PageResult<T>{private final List<T>items;private final long total;private final int page,pageSize;public PageResult(List<T>items,long total,int page,int pageSize){this.items=List.copyOf(items);this.total=total;this.page=page;this.pageSize=pageSize;}public List<T>getItems(){return items;}public long getTotal(){return total;}public int getPage(){return page;}public int getPageSize(){return pageSize;}public int getPages(){return (int)Math.ceil(total/(double)pageSize);}}
