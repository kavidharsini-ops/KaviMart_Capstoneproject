package com.kavi.kavimart.dao.jdbc;

import com.kavi.kavimart.util.PasswordUtil;import javax.sql.DataSource;import java.io.*;import java.nio.charset.StandardCharsets;import java.sql.*;import org.h2.tools.RunScript;import org.slf4j.Logger;import org.slf4j.LoggerFactory;
/** Initializes the H2 schema and applies the first-run seed script. */
public class JdbcDatabaseDao {
 private static final Logger LOG=LoggerFactory.getLogger(JdbcDatabaseDao.class);private final DataSource ds;
 /** Construct schema initialization DAO. */ public JdbcDatabaseDao(DataSource ds){this.ds=ds;}
 /** Run the idempotent schema script and seed an empty database. */ public void initialize(){run("/db/schema.sql",null);try(Connection c=ds.getConnection();PreparedStatement p=c.prepareStatement("SELECT COUNT(*) FROM users");ResultSet r=p.executeQuery()){r.next();if(r.getLong(1)==0)run("/db/seed.sql",PasswordUtil.hash("KaviAdmin123!"));}catch(SQLException e){LOG.error("Unable to initialize database",e);throw new IllegalStateException("Unable to initialize database.",e);}}
 private void run(String resource,String passwordHash){try(InputStream in=getClass().getResourceAsStream(resource)){if(in==null)throw new IllegalStateException("Missing database resource: "+resource);String script=new String(in.readAllBytes(),StandardCharsets.UTF_8);if(passwordHash!=null)script=script.replace("@@ADMIN_PASSWORD_HASH@@",passwordHash);try(Connection c=ds.getConnection();Reader reader=new StringReader(script)){RunScript.execute(c,reader);}}catch(IOException|SQLException e){LOG.error("Unable to run database script {}",resource,e);throw new IllegalStateException("Unable to initialize database.",e);}}
}
