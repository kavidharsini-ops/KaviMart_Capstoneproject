package com.kavi.kavimart.dao;

import com.kavi.kavimart.dao.jdbc.JdbcDatabaseDao;
import org.h2.jdbcx.JdbcDataSource;

final class TestDatabase {

  private TestDatabase() {
  }

  static JdbcDataSource create(String name) {
    JdbcDataSource ds = new JdbcDataSource();
    ds.setURL("jdbc:h2:mem:" + name + ";DB_CLOSE_DELAY=-1");
    ds.setUser("sa");
    new JdbcDatabaseDao(ds).initialize();
    return ds;
  }
}
