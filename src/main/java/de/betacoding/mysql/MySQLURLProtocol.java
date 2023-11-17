package de.betacoding.mysql;

import org.jetbrains.annotations.NotNull;

// Reference: https://dev.mysql.com/doc/connector-j/en/connector-j-reference-jdbc-url-format.html
public enum MySQLURLProtocol {
    JDBC_MYSQL("jdbc:mysql:"),
    JDBC_MYSQL_LOADBALANCE("jdbc:mysql:loadbalance:"),
    JDBC_MYSQL_REPLICATION("jdbc:mysql:replication:"),
    MYSQLX("mysqlx:"),
    JDBC_MYSQL_SRV("jdbc:mysql+srv:"),
    JDBC_MYSQL_SRV_LOADBALANCE("jdbc:mysql+srv:loadbalance:"),
    JDBC_MYSQL_SRV_REPLICATION("jdbc:mysql+srv:replication"),
    MYSQLX_SRV("mysqlx+srv:");

    private String str;

    MySQLURLProtocol(String str) {
        this.str = str;
    }

    @Override
    public @NotNull String toString() {
        return this.str;
    }
}
