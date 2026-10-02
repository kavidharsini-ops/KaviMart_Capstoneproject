package com.kavi.kavimart.dao.jdbc;

import com.kavi.kavimart.dao.ProductDao;
import com.kavi.kavimart.model.*;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** JDBC implementation of marketplace product persistence. */
public class JdbcProductDao implements ProductDao {
 private static final Logger LOG=LoggerFactory.getLogger(JdbcProductDao.class);private final DataSource ds;
 /** Create product persistence with the pooled data source. */ public JdbcProductDao(DataSource ds){this.ds=ds;}
 /** {@inheritDoc} */ public PageResult<Product> search(ProductQuery q){String where=" WHERE (?='' OR LOWER(p.name) LIKE ? OR LOWER(COALESCE(p.description,'')) LIKE ?) AND (?='' OR p.category=?)";String order=switch(q.getSort()){case "price_asc"->" ORDER BY p.price ASC,p.id DESC";case "price_desc"->" ORDER BY p.price DESC,p.id DESC";case "newest"->" ORDER BY p.created_at DESC,p.id DESC";default->" ORDER BY p.name ASC";};String base="SELECT p.*,u.name AS seller_name FROM products p JOIN users u ON u.id=p.seller_id"+where;String sql=base+order+" LIMIT ? OFFSET ?";String key="%"+q.getKeyword().trim().toLowerCase(Locale.ROOT)+"%";List<Product> items=new ArrayList<>();long total;
 try(Connection c=ds.getConnection();PreparedStatement p=c.prepareStatement(sql)){p.setString(1,q.getKeyword().trim());p.setString(2,key);p.setString(3,key);p.setString(4,q.getCategory());p.setString(5,q.getCategory());p.setInt(6,q.getPageSize());p.setInt(7,(q.getPage()-1)*q.getPageSize());try(ResultSet x=p.executeQuery()){while(x.next())items.add(map(x));}String countSql="SELECT COUNT(*) FROM products p"+where;try(PreparedStatement cp=c.prepareStatement(countSql)){cp.setString(1,q.getKeyword().trim());cp.setString(2,key);cp.setString(3,key);cp.setString(4,q.getCategory());cp.setString(5,q.getCategory());try(ResultSet cr=cp.executeQuery()){cr.next();total=cr.getLong(1);}}return new PageResult<>(items,total,q.getPage(),q.getPageSize());}catch(SQLException e){throw fail("search listings",e);}}
 /** {@inheritDoc} */ public Optional<Product> findById(long id){String sql="SELECT p.*,u.name AS seller_name FROM products p JOIN users u ON u.id=p.seller_id WHERE p.id=?";try(Connection c=ds.getConnection();PreparedStatement p=c.prepareStatement(sql)){p.setLong(1,id);try(ResultSet r=p.executeQuery()){return r.next()?Optional.of(map(r)):Optional.empty();}}catch(SQLException e){throw fail("find listing",e);}}
 /** {@inheritDoc} */ public List<Product> findBySeller(long id){return list("SELECT p.*,u.name AS seller_name FROM products p JOIN users u ON u.id=p.seller_id WHERE p.seller_id=? ORDER BY p.created_at DESC",id);}
 /** {@inheritDoc} */ public Product create(Product x){String sql="INSERT INTO products(seller_id,name,description,price,stock_qty,category,image_url) VALUES(?,?,?,?,?,?,?)";try(Connection c=ds.getConnection();PreparedStatement p=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){bind(p,x);p.executeUpdate();try(ResultSet k=p.getGeneratedKeys()){if(k.next())x.setId(k.getLong(1));}return x;}catch(SQLException e){throw fail("create listing",e);}}
 /** {@inheritDoc} */ public boolean updateOwned(Product x,long sellerId){String sql="UPDATE products SET name=?,description=?,price=?,stock_qty=?,category=?,image_url=? WHERE id=? AND seller_id=?";try(Connection c=ds.getConnection();PreparedStatement p=c.prepareStatement(sql)){p.setString(1,x.getName());p.setString(2,x.getDescription());p.setBigDecimal(3,x.getPrice());p.setInt(4,x.getStockQty());p.setString(5,x.getCategory());p.setString(6,x.getImageUrl());p.setLong(7,x.getId());p.setLong(8,sellerId);return p.executeUpdate()>0;}catch(SQLException e){throw fail("update listing",e);}}
 /** {@inheritDoc} */ public boolean deleteOwned(long id,long sellerId){return delete("DELETE FROM products WHERE id=? AND seller_id=?",id,sellerId);}
 /** {@inheritDoc} */ public boolean deleteAny(long id){return delete("DELETE FROM products WHERE id=?",id,0);}
 /** {@inheritDoc} */ public List<Product> findAll(){return list("SELECT p.*,u.name AS seller_name FROM products p JOIN users u ON u.id=p.seller_id ORDER BY p.created_at DESC",null);}
 private List<Product> list(String sql,Long id){List<Product> a=new ArrayList<>();try(Connection c=ds.getConnection();PreparedStatement p=c.prepareStatement(sql)){if(id!=null)p.setLong(1,id);try(ResultSet r=p.executeQuery()){while(r.next())a.add(map(r));}return a;}catch(SQLException e){throw fail("list listings",e);}}
 private boolean delete(String sql,long id,long seller){try(Connection c=ds.getConnection();PreparedStatement p=c.prepareStatement(sql)){p.setLong(1,id);if(sql.contains("seller_id=?"))p.setLong(2,seller);return p.executeUpdate()>0;}catch(SQLException e){throw fail("delete listing",e);}}
 private void bind(PreparedStatement p,Product x)throws SQLException{p.setLong(1,x.getSellerId());p.setString(2,x.getName());p.setString(3,x.getDescription());p.setBigDecimal(4,x.getPrice());p.setInt(5,x.getStockQty());p.setString(6,x.getCategory());p.setString(7,x.getImageUrl());}
 private Product map(ResultSet r)throws SQLException{return new Product(r.getLong("id"),r.getLong("seller_id"),r.getString("seller_name"),r.getString("name"),r.getString("description"),r.getBigDecimal("price"),r.getInt("stock_qty"),r.getString("category"),r.getString("image_url"),r.getTimestamp("created_at").toLocalDateTime());}
 private RuntimeException fail(String action,SQLException e){LOG.error("Database operation failed: {}",action,e);return new IllegalStateException("Database operation failed.",e);}
}
