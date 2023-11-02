package de.betacoding.mysql;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

public class MySQLConnector implements AutoCloseable {
    private final MySQLConnectionInfo connectionInfo;
    private final MySQLLogger logger;

    private Connection connection;
    private MySQLQueryService service;

    protected MySQLConnector(@NotNull MySQLConnectionInfo connectionInfo, @NotNull MySQLLogger logger) {
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

    public @NotNull Optional<MySQLQueryService> getService() {
        return Optional.ofNullable(this.service);
    }

    public void establish(@NotNull final String password) {
        Preconditions.checkNotNull(password, "Password cannot be null");

        if (this.isEstablished()) {
            this.logger.warning("Failed to establish connection: Connection is already established");
            return;
        }

        final String address = this.connectionInfo.address().getHostName() + ":" + this.connectionInfo.address().getPort();

        this.logger.info("Establishing connection to '" + address + "'");
        long millis = System.currentTimeMillis();
        try {
            this.connection = DriverManager.getConnection(this.connectionInfo.getConnectionURL(), this.connectionInfo.user(), password);
            this.service = new MySQLQueryService(this.connection);
        } catch (SQLException exception) {
            this.connection = null;
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

        final String address = this.connectionInfo.address().getHostName() + ":" + this.connectionInfo.address().getPort();

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
