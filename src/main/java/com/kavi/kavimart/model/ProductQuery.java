package com.kavi.kavimart.model;
/** Catalog query values for keyword, category, sorting, and pagination. */
public class ProductQuery {
 private String keyword="",category="",sort="newest";private int page=1,pageSize=12;
 public ProductQuery(){} public ProductQuery(String keyword,String category,String sort,int page,int pageSize){this.keyword=keyword==null?"":keyword;this.category=category==null?"":category;this.sort=sort==null?"newest":sort;this.page=Math.max(1,page);this.pageSize=Math.max(1,Math.min(48,pageSize));}
 public String getKeyword(){return keyword;}public String getCategory(){return category;}public String getSort(){return sort;}public int getPage(){return page;}public int getPageSize(){return pageSize;}
}
