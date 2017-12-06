package net.thisisz.hermes.bungee.storage.exception.driver.mysql;

import net.thisisz.hermes.bungee.storage.exception.driver.GenericDriverException;

import java.sql.SQLException;

public class MysqlSQLException extends GenericDriverException {

    private String sqlQuery;
    private SQLException sqlException;


    public MysqlSQLException(String query) {
        super("Failed to execute query: " + query);
        this.sqlQuery = query;
    }

    public MysqlSQLException(String query, SQLException exception) {
        super("Failed to execute query: " + query);
        this.sqlQuery = query;
        this.sqlException = exception;
    }

    public SQLException getSqlException() {
        return sqlException;
    }
}
