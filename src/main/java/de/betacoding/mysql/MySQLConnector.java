package de.betacoding.mysql;

import de.betacoding.util.DebugLogger;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

public class MySQLConnector implements AutoCloseable {
    private final MySQLConnectionInfo connectionInfo;
    private final DebugLogger logger;

    private Connection connection;
    private MySQLQueryService service;

    protected MySQLConnector(@NotNull MySQLConnectionInfo connectionInfo, @NotNull DebugLogger logger) {
        this.connectionInfo = connectionInfo;
        this.logger = logger;
    }

    public boolean isEstablished() {
        try {
            return this.connection != null && !this.connection.isClosed();
        } catch (SQLException exception) {
            return false;
        }
    }

    public @NotNull MySQLConnectionInfo getConnectionInfo() {
        return this.connectionInfo;
    }

    public void setDatabase(@NotNull String databaseName) {
        this.connectionInfo.setDatabaseName(databaseName);
        if (this.isEstablished()) {
            try {
                this.connection.setCatalog(databaseName);
            } catch (SQLException exception) {
                this.logger.severe("Failed to set database to '" + databaseName + "'", exception);
            }
        }
    }

    public @NotNull Optional<MySQLQueryService> getService() {
        return Optional.ofNullable(this.service);
    }

    public void establish(@NotNull final String password) {
        if (this.isEstablished()) {
            this.logger.warning("Failed to establish connection: Connection is already established");
            return;
        }

        final String address = this.connectionInfo.getHost() + ":" + this.connectionInfo.getPort();

        this.logger.info("Establishing connection to '" + address + "'");

        long millis = System.currentTimeMillis();

        try {
            this.connection = DriverManager.getConnection(this.connectionInfo.getConnectionURL(), this.connectionInfo.getUser(), password);
            this.service = new MySQLQueryService(this.connection);
        } catch (SQLException exception) {
            this.connection = null;
            if (exception.getSQLState().equals("08S01")) {
                this.logger.severe("Failed to establish connection to '" + address + "': timed out", exception);
                return;
            }
            this.logger.severe("Failed to establish connection to '" + address + "'", exception);
            return;
        }
        millis = System.currentTimeMillis()-millis;
        this.logger.info("Successfully established connection to '" + address + "' (" + millis + " ms)");
    }

    public void close() {
        if (!this.isEstablished()) {
            this.logger.warning("Failed to close connection: Connection is already closed");
            return;
        }

        final String address = this.connectionInfo.getHost() + ":" + this.connectionInfo.getPort();

        this.logger.info("Closing connection to '" + address + "'");
        long millis = System.currentTimeMillis();
        try {
            this.connection.close();
            this.connection = null;
            this.service = null;
        } catch (SQLException exception) {
            this.logger.severe("Failed to close connection to '" + address + "'" , exception);
            return;
        }
        millis = System.currentTimeMillis()-millis;
        this.logger.info("Successfully closed connection to '" + address + "' (" + millis + " ms)");
    }
}
