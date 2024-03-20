package de.betacoding.mysql;

import com.google.common.base.Preconditions;
import de.betacoding.logger.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MySQLConnectionBuilder {
    private final String protocol;
    private final String host;
    private final int port;
    private final String databaseName;
    private final String user;
    private final Map<MySQLConnectionProperty<?>, Object> properties = new HashMap<>();

    private Logger logger;

    public MySQLConnectionBuilder(@NotNull String protocol, @NotNull String host, int port, @NotNull String databaseName, @NotNull String user) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.databaseName = databaseName;
        this.user = user;
    }
    public MySQLConnectionBuilder(@NotNull String protocol, @NotNull String host, int port, @NotNull String user) {
        this(protocol, host, port, "", user);
    }
    public MySQLConnectionBuilder(@NotNull MySQLURLProtocol protocol, @NotNull String host, int port, @NotNull String databaseName, @NotNull String user) {
        this(protocol.toString(), host, port, databaseName, user);
    }
    public MySQLConnectionBuilder(@NotNull MySQLURLProtocol protocol, @NotNull String host, int port, @NotNull String user) {
        this(protocol, host, port, "", user);
    }
    @Deprecated
    public MySQLConnectionBuilder(@NotNull String protocol, @NotNull InetSocketAddress address, @NotNull String databaseName, @NotNull String user) {
        this(protocol, address.getHostName(), address.getPort(), databaseName, user);
    }

    public @NotNull <T> MySQLConnectionBuilder setProperty(@NotNull MySQLConnectionProperty<T> property, @Nullable T value) {
        if (value == null) {
            this.properties.remove(property);
        } else {
            this.properties.put(property, value);
        }
        return this;
    }

    public @NotNull MySQLConnectionBuilder setLogger(@Nullable Logger logger) {
        this.logger = logger;
        return this;
    }

    public @NotNull MySQLConnector build() {
        Preconditions.checkNotNull(this.protocol);
        Preconditions.checkNotNull(this.host);
        Preconditions.checkNotNull(this.databaseName);
        Preconditions.checkNotNull(this.user);

        return new MySQLConnector(new MySQLConnectionInfo(this.protocol, this.host, this.port, this.databaseName, this.user, Collections.unmodifiableMap(this.properties)), this.logger);
    }
}
