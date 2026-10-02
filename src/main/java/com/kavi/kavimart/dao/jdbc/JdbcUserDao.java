package com.kavi.kavimart.dao.jdbc;

import com.kavi.kavimart.dao.UserDao;
import com.kavi.kavimart.model.User;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** JDBC implementation of account persistence. */
public class JdbcUserDao implements UserDao {
 private static final Logger LOG=LoggerFactory.getLogger(JdbcUserDao.class); private final DataSource dataSource;
 /** Create the DAO with the application data source. */ public JdbcUserDao(DataSource dataSource){this.dataSource=dataSource;}
 /** {@inheritDoc} */ public Optional<User> findByEmail(String email){String sql="SELECT id,name,email,password_hash,role,is_active FROM users WHERE email=?";try(Connection c=dataSource.getConnection();PreparedStatement p=c.prepareStatement(sql)){p.setString(1,email);try(ResultSet r=p.executeQuery()){return r.next()?Optional.of(map(r)):Optional.empty();}}catch(SQLException e){throw fail("find account by email",e);}}
 /** {@inheritDoc} */ public Optional<User> findById(long id){String sql="SELECT id,name,email,password_hash,role,is_active FROM users WHERE id=?";try(Connection c=dataSource.getConnection();PreparedStatement p=c.prepareStatement(sql)){p.setLong(1,id);try(ResultSet r=p.executeQuery()){return r.next()?Optional.of(map(r)):Optional.empty();}}catch(SQLException e){throw fail("find account",e);}}
 /** {@inheritDoc} */ public User create(User u){String sql="INSERT INTO users(name,email,password_hash,role,is_active) VALUES(?,?,?,?,TRUE)";try(Connection c=dataSource.getConnection();PreparedStatement p=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){p.setString(1,u.getName());p.setString(2,u.getEmail());p.setString(3,u.getPasswordHash());p.setString(4,u.getRole());p.executeUpdate();try(ResultSet k=p.getGeneratedKeys()){if(k.next())u.setId(k.getLong(1));}return u;}catch(SQLException e){if("23505".equals(e.getSQLState()))throw new com.kavi.kavimart.exception.ConflictException("An account with this email already exists.");throw fail("create account",e);}}
 /** {@inheritDoc} */ public List<User> findAll(){String sql="SELECT id,name,email,password_hash,role,is_active FROM users ORDER BY created_at DESC";List<User> out=new ArrayList<>();try(Connection c=dataSource.getConnection();PreparedStatement p=c.prepareStatement(sql);ResultSet r=p.executeQuery()){while(r.next())out.add(map(r));return out;}catch(SQLException e){throw fail("list accounts",e);}}
 /** {@inheritDoc} */ public boolean setActive(long id,boolean active){String sql="UPDATE users SET is_active=? WHERE id=?";try(Connection c=dataSource.getConnection();PreparedStatement p=c.prepareStatement(sql)){p.setBoolean(1,active);p.setLong(2,id);return p.executeUpdate()>0;}catch(SQLException e){throw fail("change account status",e);}}
 /** {@inheritDoc} */ public long count(){try(Connection c=dataSource.getConnection();PreparedStatement p=c.prepareStatement("SELECT COUNT(*) FROM users");ResultSet r=p.executeQuery()){r.next();return r.getLong(1);}catch(SQLException e){throw fail("count accounts",e);}}
 private User map(ResultSet r)throws SQLException{return new User(r.getLong("id"),r.getString("name"),r.getString("email"),r.getString("password_hash"),r.getString("role"),r.getBoolean("is_active"));}
 private RuntimeException fail(String action,SQLException e){LOG.error("Database operation failed: {}",action,e);return new IllegalStateException("Database operation failed.",e);}
}
